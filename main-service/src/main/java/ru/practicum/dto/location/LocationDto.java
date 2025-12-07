package ru.practicum.dto.location;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    Long id;
    Double lat;
    Double lon;
}
