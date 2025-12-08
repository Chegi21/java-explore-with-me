package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    Long id;
    String title;
    String annotation;
    CategoryDto category;
    UserShortDto initiator;
    Boolean paid;
    Long confirmedRequests;
    Long views;
    LocalDateTime eventDate;
}

