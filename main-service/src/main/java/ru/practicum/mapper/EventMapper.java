package ru.practicum.mapper;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.enums.EventState;
import ru.practicum.model.CategoryEntity;
import ru.practicum.model.EventEntity;
import ru.practicum.model.UserEntity;

import java.time.LocalDateTime;

public class EventMapper {
    public static EventEntity toEntity(NewEventDto newEventDto,
                                       CategoryEntity category,
                                       LocalDateTime time,
                                       UserEntity user,
                                       EventState state) {
       EventEntity entity = new EventEntity();
       entity.setAnnotation(newEventDto.getAnnotation());
       entity.setCategory(category);
       entity.setCreatedOn(time);
       entity.setDescription(newEventDto.getDescription());
       entity.setEventDate(newEventDto.getEventDate());
       entity.setInitiator(user);
       entity.setLocationEntity(LocationMapper.toEntity(newEventDto.getLocation()));
       entity.setPaid(newEventDto.getPaid());
       entity.setParticipantLimit(newEventDto.getParticipantLimit());
       entity.setConfirmedRequests(0L);
       entity.setRequestModeration(newEventDto.getRequestModeration());
       entity.setState(state);
       entity.setTitle(newEventDto.getTitle());
       return entity;
    }

    public static EventShortDto toShortDto(EventEntity entity) {
        EventShortDto shortDto = new EventShortDto();
        shortDto.setId(entity.getId());
        shortDto.setTitle(entity.getTitle());
        shortDto.setAnnotation(entity.getAnnotation());
        shortDto.setCategory(CategoryMapper.toDto(entity.getCategory()));
        shortDto.setInitiator(UserMapper.toShortDto(entity.getInitiator()));
        shortDto.setConfirmedRequests(entity.getConfirmedRequests());
        shortDto.setEventDate(entity.getEventDate());
        shortDto.setPaid(entity.getPaid());
       return shortDto;
    }

    public static EventFullDto toFullDto(EventEntity entity) {
        EventFullDto fullDto = new EventFullDto();
        fullDto.setId(entity.getId());
        fullDto.setAnnotation(entity.getAnnotation());
        fullDto.setCategory(CategoryMapper.toDto(entity.getCategory()));
        fullDto.setCreatedOn(entity.getCreatedOn());
        fullDto.setDescription(entity.getDescription());
        fullDto.setEventDate(entity.getEventDate());
        fullDto.setInitiator(UserMapper.toShortDto(entity.getInitiator()));
        fullDto.setLocation(LocationMapper.toDto(entity.getLocationEntity()));
        fullDto.setPaid(entity.getPaid());
        fullDto.setParticipantLimit(entity.getParticipantLimit());
        fullDto.setRequestModeration(entity.getRequestModeration());
        fullDto.setState(entity.getState().name());
        fullDto.setTitle(entity.getTitle());
        return fullDto;
    }
}
