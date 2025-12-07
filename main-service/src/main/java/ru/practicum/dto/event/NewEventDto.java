package ru.practicum.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.dto.location.LocationDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    Long categoryId;
    @NotNull
    @Size(min = 20, max = 7000)
    String description;
    @NotNull
    LocalDateTime eventDate;
    @NotNull
    LocationDto location;
    Boolean paid = false;
    Long participantLimit = 0L;
    Boolean requestModeration = true;
    @NotBlank
    @Size(min = 3, max = 120)
    String title;
}

