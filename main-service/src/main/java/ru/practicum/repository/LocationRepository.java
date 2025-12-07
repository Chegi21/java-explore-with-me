package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.LocationEntity;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    @Query("FROM LocationEntity l WHERE l.lat = :lat AND l.lon = :lon")
    Optional<LocationEntity> findByLatAndLon(@Param("lat") Double lat,
                                             @Param("lon") Double lon);
}

