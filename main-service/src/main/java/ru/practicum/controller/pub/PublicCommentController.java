package ru.practicum.controller.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
@Validated
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentFullDto> getCommentList(@PathVariable Long eventId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size) {
        return commentService.getCommentListByEvent(eventId, PageRequest.of(from / size, size));
    }
}
