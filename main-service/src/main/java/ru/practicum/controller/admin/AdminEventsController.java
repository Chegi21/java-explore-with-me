package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Validated
public class AdminEventsController {
    private final EventService eventService;

    @Autowired
    public AdminEventsController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventListByAdmin(@RequestParam(name = "users", required = false) List<Long> userIdList,
                                                  @RequestParam(required = false) List<String> states,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) String rangeStart,
                                                  @RequestParam(required = false) String rangeEnd,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.getEventsByAdmin(
                userIdList,
                states,
                categories,
                rangeStart,
                rangeEnd,
                PageRequest.of(from / size, size));
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
}
