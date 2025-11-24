package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EndpointHitEntity;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHitEntity, Long> {
    @Query(
            value = "SELECT app, uri, COUNT(eh.ip) AS hits " +
                    "FROM hits h " +
                    "WHERE timestamp BETWEEN :start AND :end " +
                    "AND (:uris IS NULL OR eh.uri IN :uris) " +
                    "GROUP BY app, uri " +
                    "ORDER BY hits DESC",
            nativeQuery = true)
    List<ViewStatsDto> getViewStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query(
            value = "SELECT app, uri, COUNT(DISTINCT ip) AS hits " +
                    "FROM hits " +
                    "WHERE timestamp BETWEEN :start AND :end " +
                    "AND (:uris IS NULL OR eh.uri IN :uris) " +
                    "GROUP BY app, uri " +
                    "ORDER BY hits DESC",
            nativeQuery = true)
    List<ViewStatsDto> getUniqueViewStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);

}
