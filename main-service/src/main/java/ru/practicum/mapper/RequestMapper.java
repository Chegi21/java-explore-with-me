package ru.practicum.mapper;

import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.RequestEntity;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(RequestEntity entity) {
        return new ParticipationRequestDto(
                entity.getId(),
                entity.getCreated(),
                entity.getEvent().getId(),
                entity.getRequester().getId(),
                entity.getStatus()
        );
    }
}
