package ru.practicum.service.comment;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentShortDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentShortDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto);

    List<CommentFullDto> getCommentListByUser(Long userId, PageRequest pageRequest);

    List<CommentFullDto> getCommentListByEvent(Long eventId, PageRequest pageRequest);

    List<CommentShortDto> search(String text, PageRequest pageRequest);

    void deleteComment(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);
}
