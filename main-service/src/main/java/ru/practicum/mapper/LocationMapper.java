package ru.practicum.mapper;

import ru.practicum.dto.location.LocationDto;
import ru.practicum.model.LocationEntity;

public class LocationMapper {
    public static LocationEntity toEntity(LocationDto dto) {
        return new LocationEntity(dto.getLat(), dto.getLon());
    }

    public static LocationDto toDto(LocationEntity entity) {
        return new LocationDto(entity.getId(), entity.getLat(), entity.getLat());
    }
}
