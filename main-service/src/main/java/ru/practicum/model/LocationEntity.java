package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class LocationEntity {
    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;
}
