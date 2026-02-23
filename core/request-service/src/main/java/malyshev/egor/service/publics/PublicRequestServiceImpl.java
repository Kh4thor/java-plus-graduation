package malyshev.egor.service.publics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventState;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.dto.request.RequestStatus;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.RequestMapper;
import malyshev.egor.model.ParticipationRequest;
import malyshev.egor.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PublicRequestServiceImpl implements PublicRequestService {

    private final RequestRepository requestRepository;
    private final InteractionApiManager interactionApiManager;


    //PUBLIC
    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        return requestRepository.findAllByRequester(userId).stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    //PUBLIC
    @Override
    @Transactional
    public ParticipationRequestDto createRequest(long userId, long eventId) {

        String uri = String.format("/users/%d/requests", userId);
        EventFullDto event = interactionApiManager.getEventByPublic(eventId, uri);

        validateRequest(userId, event);

        ParticipationRequest req = new ParticipationRequest();
        req.setRequester(userId);
        req.setEvent(eventId);
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

    //PUBLIC
    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        // проверка наличия пользователя
        interactionApiManager.getUserByAdmin(userId);

        ParticipationRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Request {} not found", requestId);
                    return new NotFoundException("Request with id=" + requestId + " was not found");
                });

        if (!req.getRequester().equals(userId)) {
            log.warn("User {} cannot cancel someone else's request {}", userId, requestId);
            // 409 по нашему глобальному хендлеру
            throw new IllegalStateException("Requester mismatch for request id=" + requestId);
        }

        // Отмена пользователем -> CANCELED
        req.setStatus(RequestStatus.CANCELED);
        requestRepository.save(req);

        return RequestMapper.toRequestDto(req);
    }

    private void validateRequest(long userId, EventFullDto event) {
        // Повторный запрос
        if (requestRepository.existsByRequesterAndEvent(userId, event.getId())) {
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
            long confirmed = requestRepository.countByEventAndStatus(event.getId(), RequestStatus.CONFIRMED);
            if (confirmed >= limit) {
                log.warn("Participant limit reached for event={}", event.getId());
                throw new IllegalStateException("The participant limit has been reached");
            }
        }
    }
}