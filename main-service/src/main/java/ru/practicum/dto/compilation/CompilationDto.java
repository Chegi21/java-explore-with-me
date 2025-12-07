package ru.practicum.dto.compilation;

import lombok.*;
import ru.practicum.dto.event.EventShortDto;

import java.util.Set;

@Getter
@Setter
@Builder
public class CompilationDto {
    Long id;
    Boolean pinned;
    String title;
    Set<EventShortDto> events;

    public CompilationDto(Long id, Boolean pinned, String title, Set<EventShortDto> events) {
        this.id = id;
        this.pinned = pinned;
        this.title = title;
        this.events = events;
    }

    public CompilationDto() {
    }
}
