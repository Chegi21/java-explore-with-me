package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.CompilationEntity;

public interface CompilationRepository extends JpaRepository<CompilationEntity, Long> {
    @Query("FROM CompilationEntity c WHERE c.pinned = :pinned ORDER BY c.id DESC")
    Page<CompilationEntity> findAllByPinnedOrderByIdDesc(@Param("pinned") Boolean pinned, Pageable pageable);
}
