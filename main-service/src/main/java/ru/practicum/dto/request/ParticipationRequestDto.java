package ru.practicum.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    Long id;
    Long event;
    Long requester;
    String status;
    LocalDateTime created;
}
