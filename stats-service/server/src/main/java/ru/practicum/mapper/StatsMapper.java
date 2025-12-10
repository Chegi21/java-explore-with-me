package ru.practicum.mapper;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHitEntity;

public class StatsMapper {
    public static EndpointHitEntity toEndpointHitEntity(EndpointHitDto dto) {
        EndpointHitEntity entity = new EndpointHitEntity();
        entity.setApp(dto.getApp());
        entity.setUri(dto.getUri());
        entity.setIp(dto.getIp());
        entity.setTimestamp(dto.getTimestamp());
        return entity;
    }

    public static EndpointHitDto toEndpointHitDto(EndpointHitEntity entity) {
        return EndpointHitDto.builder()
                .id(entity.getId())
                .app(entity.getApp())
                .ip(entity.getIp())
                .uri(entity.getUri())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
