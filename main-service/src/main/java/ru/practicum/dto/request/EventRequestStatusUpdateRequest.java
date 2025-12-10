package ru.practicum.dto.request;

import lombok.*;
import ru.practicum.enums.EventState;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private EventState status;
}
