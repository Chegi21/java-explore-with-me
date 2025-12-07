package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    @Query("SELECT COUNT(c) > 0 FROM CategoryEntity c WHERE LOWER(c.name) = LOWER(:name)")
    boolean existsByName(@Param("name") String name);

}
