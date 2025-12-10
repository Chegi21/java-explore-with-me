package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.enums.EventState;
import ru.practicum.model.RequestEntity;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    Optional<RequestEntity> findByIdAndRequester_Id(Long requestId, Long userId);

    Boolean existsByRequester_IdAndEvent_Id(Long userId, Long eventId);

    List<RequestEntity> findAllByIdIn(List<Long> requestIdList);

    Long countByEvent_IdAndStatus(Long eventId, EventState state);

    @Query("FROM RequestEntity r WHERE r.requester.id = :userId AND r.event.initiator.id <> :userId")
    List<RequestEntity> findAllByRequesterIdAndNotInitiator(Long userId);

    List<RequestEntity> findAllByEvent_Initiator_IdAndEvent_Id(Long userId, Long eventId);
}
