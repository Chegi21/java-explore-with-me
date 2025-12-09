package ru.practicum.mapper;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHitEntity;

public class StatsMapper {
    public static EndpointHitEntity toEndpointHitEntity(EndpointHitDto dto) {
        return new EndpointHitEntity(dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
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
