package ru.practicum.mapper;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserEntity toUser(NewUserRequest newUserRequest) {
        return new UserEntity(newUserRequest.getName(), newUserRequest.getEmail());
    }

    public static UserDto toUserDto(UserEntity user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static UserShortDto toUserShortDto(UserEntity user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public static List<UserDto> toListDto(List<UserEntity> entityList) {
        return entityList.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
