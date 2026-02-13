package malyshev.egor.service.privates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.dto.request.EventRequestStatus;
import malyshev.egor.dto.request.EventRequestStatusUpdateRequest;
import malyshev.egor.dto.request.EventRequestStatusUpdateResult;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.mapper.RequestMapper;
import malyshev.egor.model.event.Event;
import malyshev.egor.model.request.RequestStatus;
import malyshev.egor.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RequestPrivateServiceImpl implements RequestPrivateService {

    private final RequestRepository requestRepository;
    private final InteractionApiManager interactionApiManager;

    //PRIVATE
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        Event event = interactionApiManager.getEventByUserIdAndEventId(userId, eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            // 409
            throw new IllegalStateException("Пользователь не является инициатором события");
        }
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }


    //PRIVATE
    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequests(long userId,
                                                              long eventId,
                                                              EventRequestStatusUpdateRequest body) {
        Event event = interactionApiManager.getEventByUserIdAndEventId(userId, eventId);
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
        if (action != EventRequestStatus.CONFIRMED && action != EventRequestStatus.REJECTED) {
            throw new IllegalArgumentException("status must be CONFIRMED or REJECTED");
        }

        int limit = event.getParticipantLimit();
        long alreadyConfirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (action == EventRequestStatus.CONFIRMED && limit > 0 && alreadyConfirmed >= limit) {
            throw new IllegalStateException("The participant limit has been reached");
        }

        long capacity = (limit == 0) ? Long.MAX_VALUE : Math.max(0, limit - alreadyConfirmed);

        var toUpdate = requestRepository.findAllById(body.getRequestIds())
                .stream()
                .filter(r -> r.getEvent().getId().equals(eventId))
                .toList();

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        for (var r : toUpdate) {
            if (r.getStatus() != RequestStatus.PENDING) {
                throw new IllegalStateException("Можно изменять только заявки в статусе PENDING");
            }

            if (action == EventRequestStatus.REJECTED) {
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
}