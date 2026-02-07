package ru.practicum.ewm.stats.service.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.service.model.EndpointHit;

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
        return new EndpointHitDto(
            e.getId(),
            e.getApp(),
            e.getUri(),
            e.getIp(),
            e.getHitTimestamp()
        );
    }
}
