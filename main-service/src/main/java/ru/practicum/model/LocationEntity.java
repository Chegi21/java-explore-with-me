package ru.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "locations")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "lat")
    Double lat;

    @Column(name = "lon")
    Double lon;

    public LocationEntity(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public LocationEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        LocationEntity locationEntity = (LocationEntity) o;
        return Objects.equals(id, locationEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
