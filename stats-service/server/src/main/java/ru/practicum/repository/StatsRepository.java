package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EndpointHitEntity;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHitEntity, Long> {

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHitEntity e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<ViewStatsDto> getViewStat(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHitEntity e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ViewStatsDto> getUniqueViewStat(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHitEntity e " +
            "WHERE e.timestamp BETWEEN :start AND :end AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<ViewStatsDto> getListViewStats(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHitEntity e " +
            "WHERE e.timestamp BETWEEN :start AND :end AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ViewStatsDto> getListUniqueViewStats(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end,
                                              @Param("uris") List<String> uris);
}

