package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    Long id;
    String title;
    String annotation;
    String description;
    UserShortDto initiator;
    CategoryDto category;
    LocationDto location;
    Boolean paid;
    Long participantLimit;
    Boolean requestModeration;
    Long confirmedRequests;
    Long views;
    String state;
    LocalDateTime eventDate;
    LocalDateTime createdOn;
    LocalDateTime publishedOn;

}

