package malyshev.egor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.ewm.service.event.model.Event;
import malyshev.egor.ewm.service.event.model.EventState;
import malyshev.egor.ewm.service.event.repository.EventRepository;
import malyshev.egor.ewm.service.exception.NotFoundException;
import malyshev.egor.ewm.service.request.dto.EventRequestStatusUpdateRequest;
import malyshev.egor.ewm.service.request.dto.EventRequestStatusUpdateResult;
import malyshev.egor.ewm.service.request.dto.ParticipationRequestDto;
import malyshev.egor.ewm.service.request.mapper.RequestMapper;
import malyshev.egor.ewm.service.request.model.ParticipationRequest;
import malyshev.egor.ewm.service.request.model.RequestStatus;
import malyshev.egor.ewm.service.request.repository.RequestRepository;
import malyshev.egor.ewm.service.user.model.User;
import malyshev.egor.ewm.service.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static malyshev.egor.ewm.service.request.dto.EventRequestStatusUpdateRequest.Status.CONFIRMED;
import static malyshev.egor.ewm.service.request.dto.EventRequestStatusUpdateRequest.Status.REJECTED;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        assertUserExists(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event {} not found", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        validateRequest(userId, event);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User {} not found", userId);
                    return new NotFoundException("User with id=" + userId + " was not found");
                });

        ParticipationRequest req = new ParticipationRequest();
        req.setRequester(user);
        req.setEvent(event);
        req.setCreated(LocalDateTime.now());

        // Автоподтверждение: либо модерация отключена, либо лимит = 0 (без ограничений)
        boolean unlimited = event.getParticipantLimit() == 0;
        boolean autoConfirm = !event.isRequestModeration() || unlimited;

        if (autoConfirm) {
            req.setStatus(RequestStatus.CONFIRMED);
            // счётчик подтверждений считаем по таблице запросов (через репозиторий), Event не трогаем
        } else {
            req.setStatus(RequestStatus.PENDING);
        }

        req = requestRepository.save(req);
        return RequestMapper.toRequestDto(req);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        assertUserExists(userId);

        ParticipationRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Request {} not found", requestId);
                    return new NotFoundException("Request with id=" + requestId + " was not found");
                });

        if (!req.getRequester().getId().equals(userId)) {
            log.warn("User {} cannot cancel someone else's request {}", userId, requestId);
            // 409 по нашему глобальному хендлеру
            throw new IllegalStateException("Requester mismatch for request id=" + requestId);
        }

        // Отмена пользователем -> CANCELED
        req.setStatus(RequestStatus.CANCELED);
        requestRepository.save(req);

        return RequestMapper.toRequestDto(req);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие " + eventId + " не найдено"));

        if (!event.getInitiator().getId().equals(userId)) {
            // 409
            throw new IllegalStateException("Пользователь не является инициатором события");
        }

        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequests(long userId, long eventId, EventRequestStatusUpdateRequest body) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие " + eventId + " не найдено"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new IllegalStateException("Пользователь не является инициатором события");
        }
        if (body == null || body.getRequestIds() == null || body.getRequestIds().isEmpty()) {
            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(List.of())
                    .rejectedRequests(List.of())
                    .build();
        }

        var action = body.getStatus();
        if (action != CONFIRMED && action != REJECTED) {
            throw new IllegalArgumentException("status must be CONFIRMED or REJECTED");
        }

        int limit = event.getParticipantLimit();
        long alreadyConfirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (action == CONFIRMED && limit > 0 && alreadyConfirmed >= limit) {
            throw new IllegalStateException("The participant limit has been reached");
        }

        long capacity = (limit == 0) ? Long.MAX_VALUE : Math.max(0, limit - alreadyConfirmed);

        var toUpdate = requestRepository.findAllById(body.getRequestIds())
                .stream()
                .filter(r -> r.getEvent().getId().equals(eventId))
                .toList();

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected  = new ArrayList<>();

        for (var r : toUpdate) {
            if (r.getStatus() != RequestStatus.PENDING) {
                throw new IllegalStateException("Можно изменять только заявки в статусе PENDING");
            }

            if (action == REJECTED) {
                r.setStatus(RequestStatus.REJECTED);
                rejected.add(RequestMapper.toRequestDto(r));
            } else { // CONFIRMED
                if (capacity > 0) {
                    r.setStatus(RequestStatus.CONFIRMED);
                    confirmed.add(RequestMapper.toRequestDto(r));
                    capacity--;
                } else {
                    // сюда попадём только если capacity закончился в процессе —
                    // такие заявки переводим в REJECTED по ТЗ
                    r.setStatus(RequestStatus.REJECTED);
                    rejected.add(RequestMapper.toRequestDto(r));
                }
            }
        }

        requestRepository.saveAll(toUpdate);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
    }

    private void validateRequest(long userId, Event event) {
        // Повторный запрос
        if (requestRepository.existsByRequesterIdAndEventId(userId, event.getId())) {
            log.warn("Duplicate participation request by user={} for event={}", userId, event.getId());
            throw new IllegalStateException("Request already exists");
        }
        // Инициатор не может подать на своё событие
        if (event.getInitiator().getId().equals(userId)) {
            log.warn("Initiator {} cannot request participation in own event {}", userId, event.getId());
            throw new IllegalStateException("Initiator cannot send participation request");
        }
        // Только для опубликованных
        if (event.getState() != EventState.PUBLISHED) {
            log.warn("Event {} is not published", event.getId());
            throw new IllegalStateException("Cannot participate in unpublished event");
        }
        // Лимит мест: учитываем только если > 0
        int limit = event.getParticipantLimit();
        if (limit > 0) {
            long confirmed = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
            if (confirmed >= limit) {
                log.warn("Participant limit reached for event={}", event.getId());
                throw new IllegalStateException("The participant limit has been reached");
            }
        }
    }

    private void assertUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }
}
