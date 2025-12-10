package ru.practicum.service.request;

import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResponse;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsByCurrentUserOfCurrentEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResponse updateRequest(Long userId,
                                                   Long eventId,
                                                   EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> getRequestsByCurrentUser(Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
