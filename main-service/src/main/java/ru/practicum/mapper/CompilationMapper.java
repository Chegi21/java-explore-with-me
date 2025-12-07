package ru.practicum.mapper;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.CompilationEntity;
import ru.practicum.model.EventEntity;

import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationEntity toEntity(NewCompilationDto dto, Set<EventEntity> entitySet) {
        return new CompilationEntity(dto.getPinned(), dto.getTitle(), entitySet);
    }

    public static CompilationDto toDto(CompilationEntity entity) {
        Set<EventShortDto> shortDtoList = entity.getEvents().stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toSet());
        return new CompilationDto(entity.getId(), entity.getPinned(), entity.getTitle(), shortDtoList);
    }
}
