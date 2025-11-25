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
            value = "SELECT app, uri, COUNT(s.ip) AS hits " +
                    "FROM stats s " +
                    "WHERE s.timestamp BETWEEN :start AND :end " +
                    "GROUP BY app, uri " +
                    "ORDER BY hits DESC",
            nativeQuery = true)
    List<ViewStatsDto> getViewStat(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(
            value = "SELECT app, uri, COUNT(DISTINCT ip) AS hits " +
                    "FROM stats s " +
                    "WHERE s.timestamp BETWEEN :start AND :end " +
                    "GROUP BY app, uri " +
                    "ORDER BY hits DESC",
            nativeQuery = true)
    List<ViewStatsDto> getUniqueViewStat(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(
            value = "SELECT app, uri, COUNT(s.ip) AS hits " +
                    "FROM stats s " +
                    "WHERE timestamp BETWEEN :start AND :end " +
                    "AND s.uri IN :uris " +
                    "GROUP BY app, uri " +
                    "ORDER BY hits DESC",
            nativeQuery = true)
    List<ViewStatsDto> getListViewStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query(
            value = "SELECT app, uri, COUNT(DISTINCT s.ip) AS hits " +
                    "FROM stats s " +
                    "WHERE timestamp BETWEEN :start AND :end " +
                    "AND s.uri IN :uris " +
                    "GROUP BY app, uri " +
                    "ORDER BY hits DESC",
            nativeQuery = true)
    List<ViewStatsDto> getListUniqueViewStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );
}
