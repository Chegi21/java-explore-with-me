package ru.practicum.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentShortDto addComment(@PathVariable Long userId,
                                      @PathVariable Long eventId,
                                      @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/users/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentShortDto updateComment(@PathVariable Long userId,
                                         @PathVariable Long commentId,
                                         @Valid @RequestBody NewCommentDto updateCommentDto) {
        return commentService.updateComment(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("/users/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
    }

    @GetMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentFullDto> getCommentListByUser(@PathVariable Long userId,
                                                     @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return commentService.getCommentListByUser(userId, PageRequest.of(from / size, size));
    }
}
