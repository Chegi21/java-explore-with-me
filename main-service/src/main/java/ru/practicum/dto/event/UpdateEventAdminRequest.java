package ru.practicum.dto.event;

import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.enums.EventStateAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    String annotation;
    Long categoryId;
    @Size(min = 20, max = 7000)
    String description;
    LocalDateTime eventDate;
    LocationDto location;
    Boolean paid;
    Long participantLimit;
    Boolean requestModeration;
    EventStateAction state;
    @Size(min = 3, max = 120)
    String title;
}

