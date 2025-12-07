package ru.practicum.dto.request;

import lombok.*;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    Long id;
    LocalDateTime created;
    Long event;
    Long requester;
    EventState status;
}
