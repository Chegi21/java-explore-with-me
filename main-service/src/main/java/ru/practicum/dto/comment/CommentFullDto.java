package ru.practicum.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.EventEntity;
import ru.practicum.model.UserEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentFullDto {
    private Long id;
    private EventEntity event;
    private UserEntity author;
    private String text;
    private LocalDateTime created;
}
