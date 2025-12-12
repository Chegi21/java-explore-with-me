package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.CommentEntity;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findAllByEvent_Id(Long eventId, Pageable pageable);

    List<CommentEntity> findByAuthor_Id(Long userId);

    @Query("SELECT c " +
            "FROM comments as c " +
            "WHERE LOWER(c.text) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<CommentEntity> search(@Param("text") String text, Pageable pageable);

}
