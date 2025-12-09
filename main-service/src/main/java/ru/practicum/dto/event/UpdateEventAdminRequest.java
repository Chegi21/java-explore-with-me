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
    private String title;

    @Size(min = 20, max = 2000)
    private String annotation;

    @Size(min = 20, max = 7000)
    private String description;

    @PositiveOrZero(message = "Лимит участников должен быть положительным число")
    private Long participantLimit;

    private LocalDateTime eventDate;
    private Long category;
    private LocationDto location;
    private Boolean paid;
    private Boolean requestModeration;
    private StateAction stateAction;
}

