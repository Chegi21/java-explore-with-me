package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.UserEntity;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u WHERE u.id IN :idList")
    Page<UserEntity> findAllByIdIn(@Param("idList") List<Long> idList, Pageable pageable);

    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.name = :name")
    Boolean existsByName(@Param("name") String name);

    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.id IN :userIds")
    Boolean existsByIdIn(@Param("userIds") List<Long> userIds);
}
