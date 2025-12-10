package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.UserEntity;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Page<UserEntity> findAllByIdIn(List<Long> idList, Pageable pageable);

    Boolean existsByName(String name);

    Boolean existsByIdIn(List<Long> userIds);
}
