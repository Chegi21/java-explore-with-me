package ru.practicum.controller.priv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResponse;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.event.EventService;
import ru.practicum.service.request.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@Validated
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @Autowired
    public PrivateEventController(EventService eventService, RequestService requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByInitiator(@PathVariable("userId") Long userId,
                                                    @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return eventService.getEventsByInitiator(userId, PageRequest.of(from / size, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable("userId") Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByInitiator(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
        return eventService.getEventByInitiator(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByInitiator(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventByInitiator(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByCurrentUserOfCurrentEvent(@PathVariable Long userId,
                                                                                @PathVariable Long eventId) {
        return requestService.getRequestsByCurrentUserOfCurrentEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResponse updateRequest(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.updateRequest(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
