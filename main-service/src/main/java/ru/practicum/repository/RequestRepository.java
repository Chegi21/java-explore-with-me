package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.enums.EventState;
import ru.practicum.model.RequestEntity;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    @Query("FROM RequestEntity r WHERE r.id = :requestId AND r.requester.id = :userId")
    Optional<RequestEntity> findByIdAndRequesterId(@Param("requestId") Long requestId,
                                                   @Param("userId") Long userId);

    @Query("SELECT COUNT(r) > 0 FROM RequestEntity r " +
            "WHERE r.requester.id = :userId AND r.event.id = :eventId")
    Boolean existsByRequesterIdAndEventId(@Param("userId") Long userId,
                                          @Param("eventId") Long eventId);

    @Query("FROM RequestEntity r WHERE r.id IN :requestIdList")
    List<RequestEntity> findAllByIdIn(@Param("requestIdList") List<Long> requestIdList);

    @Query("SELECT COUNT(r) FROM RequestEntity r WHERE r.event.id = :eventId AND r.status = :state")
    Long countByEventIdAndStatus(@Param("eventId") Long eventId,
                                 @Param("state") EventState state);

    @Query("FROM RequestEntity r WHERE r.requester.id = :userId AND r.event.initiator.id <> :userId")
    List<RequestEntity> findAllByRequesterIdAndNotInitiator(Long userId);

    @Query("FROM RequestEntity r WHERE r.event.initiator.id = :userId AND r.event.id = :eventId")
    List<RequestEntity> findAllByEvent_InitiatorIdAndEvent_Id(@Param("userId") Long userId,
                                                              @Param("eventId") Long eventId);
}
