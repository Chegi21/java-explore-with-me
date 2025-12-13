package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.UserEntity;
import ru.practicum.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUserList(List<Long> idList, Pageable pageable) {
        log.info("Запрос на список пользователей c id: {}", idList);

        if (idList == null) {
            List<UserDto> dtoList = UserMapper.toListDto(userRepository.findAll(pageable).toList());
            log.info("Найден список в количестве {}", dtoList.size());
            return dtoList;
        } else {
            List<UserDto> dtoList = new ArrayList<>();
            if (userRepository.existsByIdIn(idList)) {
                List<UserEntity> entityList = userRepository.findAllByIdIn(idList, pageable).toList();
                dtoList = UserMapper.toListDto(entityList);
            }
            log.info("Найден список в количестве {}", dtoList.size());
            return dtoList;
        }
    }

    @Transactional
    @Override
    public UserDto addUser(NewUserRequest userDto) {
        log.info("Запрос на добавление нового пользователя");

        if (userRepository.existsByName(userDto.getName())) {
            log.warn("Пользователь с именем {} уже существует", userDto.getName());
            throw new ConflictException("Пользователь с таким именем уже существует");
        }

        UserEntity entity = UserMapper.toEntity(userDto);
        UserDto createDto = UserMapper.toDto(userRepository.save(entity));

        log.info("Пользователь с id = {} успешно создан", createDto.getId());
        return createDto;
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);

        UserEntity entity = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });

        userRepository.deleteById(userId);

        log.info("Пользователь с id = {} успешно удален", entity.getId());
    }
}
