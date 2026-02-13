package malyshev.egor.service.privates;


import malyshev.egor.dto.request.EventRequestStatusUpdateRequest;
import malyshev.egor.dto.request.EventRequestStatusUpdateResult;
import malyshev.egor.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestPrivateService {

    // PRIVATE
    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);

    // PRIVATE
    EventRequestStatusUpdateResult updateEventRequests(long userId, long eventId, EventRequestStatusUpdateRequest body);
}
