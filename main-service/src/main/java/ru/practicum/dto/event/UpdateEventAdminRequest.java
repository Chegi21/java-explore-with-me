package ru.practicum.dto.event;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.enums.StateAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 3, max = 120)
    String title;

    @Size(min = 20, max = 2000)
    String annotation;

    @Size(min = 20, max = 7000)
    String description;

    @PositiveOrZero(message = "Лимит участников должен быть положительным число")
    Long participantLimit;

    LocalDateTime eventDate;
    Long category;
    LocationDto location;
    Boolean paid;
    Boolean requestModeration;
    StateAction stateAction;
}

