package ru.practicum.dto.location;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull(message = "Широта не должна быть null")
    private Double lat;

    @NotNull(message = "Долгота не должна быть null")
    private Double lon;
}
