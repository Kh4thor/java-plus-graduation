package malyshev.egor.request.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.request.dto.ParticipationRequestDto;
import malyshev.egor.request.model.ParticipationRequest;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated());
        dto.setEvent(request.getEvent().getId());
        dto.setRequester(request.getRequester().getId());
        dto.setStatus(request.getStatus().name());
        return dto;
    }
}
