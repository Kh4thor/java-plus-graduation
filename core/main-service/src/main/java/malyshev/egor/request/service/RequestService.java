package malyshev.egor.request.service;


import malyshev.egor.request.dto.EventRequestStatusUpdateRequest;
import malyshev.egor.request.dto.EventRequestStatusUpdateResult;
import malyshev.egor.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto createRequest(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateEventRequests(long userId, long eventId, EventRequestStatusUpdateRequest body);
}
