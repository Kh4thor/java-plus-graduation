package malyshev.egor.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.model.request.ParticipationRequest;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated());
        dto.setEvent(request.getEvent().getId());
        dto.setRequester(request.getRequester().getId());
        dto.setStatus(request.getStatus());
        return dto;
    }
}
