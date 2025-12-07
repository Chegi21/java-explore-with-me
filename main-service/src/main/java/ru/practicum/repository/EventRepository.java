package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.enums.EventState;
import ru.practicum.model.EventEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END " +
            "FROM EventEntity e WHERE e.category.id = :categoryId")
    boolean existsByCategoryId(@Param("categoryId")Long categoryId);

    @Query("FROM EventEntity e WHERE e.id IN :ids")
    Set<EventEntity> findAllById(Set<Long> ids);

    @Query(
            value = "SELECT e FROM EventEntity e WHERE e.initiator.id = :userId",
            countQuery = "SELECT COUNT(e) FROM EventEntity e WHERE e.initiator.id = :userId"
    )
    Page<EventEntity> findAllByInitiatorId(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT e FROM EventEntity e WHERE e.id = :eventId AND e.initiator.id = :userId")
    Optional<EventEntity> findByIdAndInitiatorId(@Param("eventId") Long eventId,
                                                 @Param("userId") Long userId);


    @Query("FROM EventEntity e WHERE e.id = :eventId AND e.state = :state")
    Optional<EventEntity> findByIdAndState(@Param("eventId") Long eventId,
                                           @Param("state") EventState state);

    @Query("""
            SELECT e
              FROM EventEntity e
             WHERE e.state = 'PUBLISHED'
               AND (:text IS NULL
                    OR (LOWER(e.annotation) LIKE %:text%
                        OR LOWER(e.description) LIKE %:text%
                    )
               )
               AND (:categories IS NULL OR e.category.id IN :categories)
               AND (:paid IS NULL OR e.paid = :paid)
               AND e.eventDate >= COALESCE(:rangeStart, e.eventDate)
               AND e.eventDate <= COALESCE(:rangeEnd,   e.eventDate)
               AND (:onlyAvailable IS NULL
                    OR :onlyAvailable = false
                    OR COALESCE(e.participantLimit, 0) = 0
                    OR COALESCE(e.confirmedRequests, 0) < COALESCE(e.participantLimit, 0)
               )
            """)
    List<EventEntity> searchPublishedEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable
    );






    @Query(
            value = "SELECT * FROM events e " +
                    "WHERE (:userIds IS NULL OR e.initiator_id IN (:userIds)) " +
                    "AND (:states IS NULL OR e.state IN (:states)) " +
                    "AND (:categories IS NULL OR e.category_id IN (:categories)) " +
                    "AND (:rangeStart IS NULL OR e.event_date >= :rangeStart) " +
                    "AND (:rangeEnd IS NULL OR e.event_date <= :rangeEnd)",
            nativeQuery = true
    )
    Page<EventEntity> findEvents(@Param("userIds") List<Long> userIds,
                           @Param("states") List<EventState> states,
                           @Param("categories") List<Long> categories,
                           @Param("rangeStart") LocalDateTime rangeStart,
                           @Param("rangeEnd") LocalDateTime rangeEnd,
                           Pageable pageable);
}
