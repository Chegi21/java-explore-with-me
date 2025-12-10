package ru.practicum.service.event;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.*;

import java.util.List;

public interface EventService {
    List<EventShortDto> getEventsByInitiator(Long userId, Pageable pageable);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByInitiator(Long userId, Long eventId);

    EventFullDto updateEventByInitiator(Long userId, Long eventId, UpdateEventUserRequest newEvent);

    List<EventFullDto> getEventsByAdmin(List<Long> userIdList,
                                        List<String> states,
                                        List<Long> categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        PageRequest pageRequest);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest newEvent);

    List<EventShortDto> getEventList(String text,
                                     List<Long> categoryIdList,
                                     Boolean paid,
                                     String rangeStart,
                                     String rangeEnd,
                                     Boolean onlyAvailable,
                                     String sort,
                                     PageRequest pageRequest,
                                     String userIp,
                                     String requestUri);

    EventFullDto getEvent(Long eventId, String userIp, String requestUri);
}
