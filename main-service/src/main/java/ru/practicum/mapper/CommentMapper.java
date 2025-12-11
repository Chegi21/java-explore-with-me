package ru.practicum.mapper;

import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.CommentEntity;
import ru.practicum.model.EventEntity;
import ru.practicum.model.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentShortDto toShortDto(CommentEntity comment) {
        CommentShortDto shortDto = new CommentShortDto();
        shortDto.setId(comment.getId());
        shortDto.setAuthorId(comment.getAuthor().getId());
        shortDto.setEventId(comment.getEvent().getId());
        shortDto.setText(comment.getText());
        shortDto.setCreated(comment.getCreated());
        return shortDto;
    }

    public static CommentFullDto toFullDto(CommentEntity comment) {
        CommentFullDto fullDto = new CommentFullDto();
        fullDto.setId(comment.getId());
        fullDto.setAuthor(comment.getAuthor());
        fullDto.setEvent(comment.getEvent());
        fullDto.setText(comment.getText());
        fullDto.setCreated(comment.getCreated());
        return fullDto;
    }

    public static CommentEntity toEntity(NewCommentDto commentDto, EventEntity event, UserEntity user) {
        CommentEntity entity = new CommentEntity();
        entity.setText(commentDto.getText());
        entity.setEvent(event);
        entity.setAuthor(user);
        return entity;
    }

    public static List<CommentFullDto> toFullDtoList(List<CommentEntity> entityList) {
        return entityList.stream().map(CommentMapper::toFullDto).collect(Collectors.toList());
    }

    public static List<CommentShortDto> toShortDtoList(List<CommentEntity> entityList) {
        return entityList.stream().map(CommentMapper::toShortDto).collect(Collectors.toList());
    }

}
