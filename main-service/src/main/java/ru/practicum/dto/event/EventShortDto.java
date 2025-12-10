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
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private UserShortDto initiator;
    private Boolean paid;
    private Long confirmedRequests;
    private Long views;
    private LocalDateTime eventDate;
}

