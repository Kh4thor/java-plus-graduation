package malyshev.egor.service.publics;

import malyshev.egor.dto.request.ParticipationRequestDto;

import java.util.List;

public interface PublicRequestService {

    // PUBLIC
    List<ParticipationRequestDto> getUserRequests(long userId);


    // PUBLIC
    ParticipationRequestDto createRequest(long userId, long eventId);


    // PUBLIC
    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
