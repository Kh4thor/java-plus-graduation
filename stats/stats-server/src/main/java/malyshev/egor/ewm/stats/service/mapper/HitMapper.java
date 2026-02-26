package malyshev.egor.ewm.stats.service.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.ewm.stats.dto.EndpointHitDto;
import malyshev.egor.ewm.stats.service.model.EndpointHit;

@UtilityClass
public class HitMapper {

    public EndpointHit toEndpointHit(EndpointHitDto dto) {
        EndpointHit e = new EndpointHit();
        e.setApp(dto.getApp());
        e.setUri(dto.getUri());
        e.setIp(dto.getIp());
        e.setHitTimestamp(dto.getTimestamp());
        return e;
    }

    public EndpointHitDto toDto(EndpointHit e) {
        return EndpointHitDto.builder()
                .id(e.getId())
                .app(e.getApp())
                .uri(e.getUri())
                .ip(e.getIp())
                .timestamp(e.getHitTimestamp())
                .build();
    }
}
