package ru.practicum.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentShortDto {
    private Long id;
    private Long authorId;
    private Long eventId;
    private String text;
    private LocalDateTime created;
}
