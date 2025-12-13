package ru.practicum.service.event;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.event.*;
import ru.practicum.enums.EventState;
import ru.practicum.enums.StateAction;
import ru.practicum.enums.SortValue;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.DateMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.CategoryEntity;
import ru.practicum.model.EventEntity;
import ru.practicum.model.LocationEntity;
import ru.practicum.model.UserEntity;
import ru.practicum.repository.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImp implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    @Value("${app.name}")
    private String appName;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsByInitiator(Long userId, Pageable pageable) {
        log.info("Запрос на список событий пользователем с id = {}", userId);

        List<EventEntity> entityList = eventRepository
                .findAllByInitiator_Id(userId, pageable)
                .toList();

        List<EventShortDto> dtoList = entityList.stream()
                .map(EventMapper::toShortDto)
                .map(this::addDtoWithConfirmedRequestsAndViews)
                .toList();

        log.info("Найден список событий в размере {}", dtoList.size());
        return dtoList;
    }

    @Transactional
    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        log.info("Запрос на создание события от пользователя с id = {}", userId);

        UserEntity findUser = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });

        CategoryEntity findCategory = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> {
            log.warn("Категория с id = {} не найдена", newEventDto.getCategory());
            return new NotFoundException("Категория для нового события не найдена");
        });

        LocalDateTime now = LocalDateTime.now();
        if (newEventDto.getEventDate().minusHours(2).isBefore(now)) {
            log.warn("Дата и время события не могут быть раньше, чем через два часа от текущего момента");
            throw new ValidationException("Дата и время события не могут быть раньше, чем через два часа от текущего момента");
        }

        EventEntity createEntity = EventMapper.toEntity(newEventDto, findCategory, now, findUser, EventState.PENDING);

        EventEntity saveEntity = eventRepository.save(createEntity);

        EventFullDto fullDto = addDtoWithConfirmedRequestsAndViews(EventMapper.toFullDto(saveEntity));

        log.info("Событие {} успешно создано", fullDto.toString());
        return fullDto;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByInitiator(Long userId, Long eventId) {
        log.info("Запрос на получение события с id = {} от пользователя с id = {}", eventId, userId);

        EventEntity findEntity = eventRepository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(() -> {
            log.warn("Событие с id = {} для пользователя с id = {} не найдено", eventId, userId);
            return new NotFoundException("Событие не найдено");
        });

        EventFullDto fullDto = addDtoWithConfirmedRequestsAndViews(EventMapper.toFullDto(findEntity));

        log.info("Событие с id = {} успешно получено пользователем с id = {}", fullDto.getId(), userId);
        return fullDto;
    }

    @Transactional
    @Override
    public EventFullDto updateEventByInitiator(Long userId, Long eventId, UpdateEventUserRequest newEvent) {
        log.info("Запрос на изменение события с id = {} от пользователя с id = {} статус {}", eventId, userId, newEvent.getStateAction());

        EventEntity oldEvent = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id = {} не найдено", eventId);
            return new NotFoundException("Событие не найдено");
        });

        if (!Objects.equals(oldEvent.getInitiator().getId(), userId)) {
            log.warn("Пользователь c id = {} не является инициатором события", userId);
            throw new ConflictException("Пользователь не является инициатором события");
        }

        if (newEvent.getEventDate() != null) {
            if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                log.warn("Дата и время события не могут быть раньше, чем через два часа от текущего момента");
                throw new ValidationException(
                        "Дата и время события не могут быть раньше, чем через два часа от текущего момента"
                );
            }
        }

        if (oldEvent.getState() == EventState.CANCELED || oldEvent.getState() == EventState.PENDING) {

            if (newEvent.getStateAction() == StateAction.SEND_TO_REVIEW) {
                oldEvent.setState(EventState.PENDING);
            }

            if (newEvent.getStateAction() == StateAction.CANCEL_REVIEW) {
                oldEvent.setState(EventState.CANCELED);
            }

        } else {
            log.warn("Статус события {} не соответствует CANCELED или PENDING", oldEvent.getState());
            throw new ConflictException("Изменение возможно только для событий в статусе: CANCELED или PENDING");
        }

        oldEvent.setAnnotation(Objects.requireNonNullElse(newEvent.getAnnotation(), oldEvent.getAnnotation()));
        oldEvent.setDescription(Objects.requireNonNullElse(newEvent.getDescription(), oldEvent.getDescription()));
        oldEvent.setEventDate(Objects.requireNonNullElse(newEvent.getEventDate(), oldEvent.getEventDate()));
        oldEvent.setPaid(Objects.requireNonNullElse(newEvent.getPaid(), oldEvent.getPaid()));
        oldEvent.setParticipantLimit(Objects.requireNonNullElse(newEvent.getParticipantLimit(), oldEvent.getParticipantLimit()));
        oldEvent.setRequestModeration(Objects.requireNonNullElse(newEvent.getRequestModeration(), oldEvent.getRequestModeration()));
        oldEvent.setTitle(Objects.requireNonNullElse(newEvent.getTitle(), oldEvent.getTitle()));

        if (newEvent.getCategory() != null) {
            CategoryEntity findCategory = categoryRepository.findById(newEvent.getCategory()).orElseThrow(() -> {
                log.warn("Категория с id = {} не найдена", newEvent.getCategory());
                return new NotFoundException("Категория не найдена");
            });
            oldEvent.setCategory(findCategory);
        }

        if (newEvent.getLocation() != null) {
            LocationEntity location = new LocationEntity();
            location.setLon(newEvent.getLocation().getLon());
            location.setLat(newEvent.getLocation().getLat());

            oldEvent.setLocationEntity(location);
        }

        EventEntity updateEntity = eventRepository.save(oldEvent);
        EventFullDto updateDto = addDtoWithConfirmedRequestsAndViews(EventMapper.toFullDto(updateEntity));

        log.info("Событие с id = {} успешно обновлено", updateDto.getId());
        return updateDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> userIdList, List<String> states, List<Long> categories,
                                               String rangeStart, String rangeEnd, PageRequest pageRequest) {
        log.info("Получен запрос на список событий");

        LocalDateTime start = (rangeStart == null || rangeStart.isEmpty())
                ? null : DateMapper.toLocalDateTime(rangeStart);

        LocalDateTime end = (rangeEnd == null || rangeEnd.isEmpty())
                ? null : DateMapper.toLocalDateTime(rangeEnd);

        if (start != null && end != null && !start.isBefore(end)) {
            throw new ValidationException("Дата старта фильтра должна быть раньше даты окончания фильтра");
        }

        List<EventState> eventStates = null;
        if (states != null && !states.isEmpty()) {
            eventStates = states.stream()
                    .map(EventState::valueOf)
                    .toList();
        }

        List<EventEntity> entityList = eventRepository
                .findEvents(userIdList, eventStates, categories, start, end, pageRequest)
                .getContent();

        List<EventFullDto> dtoList = entityList.stream()
                .map(EventMapper::toFullDto)
                .map(this::addDtoWithConfirmedRequestsAndViews)
                .toList();

        log.info("Найден список в количестве {}", dtoList.size());
        return dtoList;
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest newEvent) {
        log.info("Запрос на обновление события с id = {} и статусом {}", eventId, newEvent.getStateAction());

        EventEntity oldEvent = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id = {} не найдено", eventId);
            return new NotFoundException("Событие не найдено");
        });

        if (newEvent.getEventDate() != null) {
            if (oldEvent.getEventDate().minusHours(1L).isBefore(LocalDateTime.now())) {
                log.warn("Событие можно менять не позднее чем за час до начала");
                throw new ConflictException("Событие можно менять не позднее чем за час до начала");
            }
        }

        if (newEvent.getEventDate() != null) {
            if (newEvent.getEventDate().isBefore(LocalDateTime.now())) {
                log.warn("Дата события не может быть в прошлом");
                throw new ValidationException("Дата события не может быть в прошлом");
            }
        }

        if (newEvent.getStateAction() != null) {
            if (newEvent.getStateAction() == StateAction.PUBLISH_EVENT) {

                if (oldEvent.getState() != EventState.PENDING) {
                    log.warn("Публиковать можно только события cо статусом PENDING");
                    throw new ConflictException("Публиковать можно только события ожидающие публикации");
                }

                oldEvent.setState(EventState.PUBLISHED);
                oldEvent.setPublishedOn(LocalDateTime.now());

            } else if (newEvent.getStateAction() == StateAction.REJECT_EVENT) {

                if (oldEvent.getState().equals(EventState.PUBLISHED)) {
                    log.warn("Статус события не может быть PUBLISHED для отклонения");
                    throw new ConflictException("Статус события не может быть PUBLISHED для отклонения");
                }

                oldEvent.setState(EventState.CANCELED);

            } else {
                throw new ValidationException("Указан некорректный для данного метода stateAction");
            }
        }

        oldEvent.setAnnotation(Objects.requireNonNullElse(newEvent.getAnnotation(), oldEvent.getAnnotation()));
        oldEvent.setDescription(Objects.requireNonNullElse(newEvent.getDescription(), oldEvent.getDescription()));
        oldEvent.setEventDate(Objects.requireNonNullElse(newEvent.getEventDate(), oldEvent.getEventDate()));
        oldEvent.setPaid(Objects.requireNonNullElse(newEvent.getPaid(), oldEvent.getPaid()));
        oldEvent.setParticipantLimit(Objects.requireNonNullElse(newEvent.getParticipantLimit(), oldEvent.getParticipantLimit()));
        oldEvent.setRequestModeration(Objects.requireNonNullElse(newEvent.getRequestModeration(), oldEvent.getRequestModeration()));
        oldEvent.setTitle(Objects.requireNonNullElse(newEvent.getTitle(), oldEvent.getTitle()));

        oldEvent.setCategory(newEvent.getCategory() == null
                        ? oldEvent.getCategory()
                        : categoryRepository.findById(newEvent.getCategory()).orElseThrow(() -> {
                    log.warn("Категория с id = {} не найдена", newEvent.getCategory());
                    return new NotFoundException("Категория для нового события не найдена");
                })
        );

        oldEvent.setLocationEntity(newEvent.getLocation() == null
                ? oldEvent.getLocationEntity()
                : new LocationEntity(newEvent.getLocation().getLat(), newEvent.getLocation().getLon()));

        EventEntity createEntity = eventRepository.save(oldEvent);
        EventFullDto updateFullDto = addDtoWithConfirmedRequestsAndViews(EventMapper.toFullDto(createEntity));

        log.info("Событие с id = {} успешно обновлено", updateFullDto.getId());
        return updateFullDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventList(String text, List<Long> categoryIdList, Boolean paid, String rangeStart,
                                            String rangeEnd, Boolean onlyAvailable, String sort, PageRequest pageRequest,
                                            String userIp, String requestUri) {
        log.info("Запрос на список событий с текстом поиска: {}", text);

        LocalDateTime start = (rangeStart == null || rangeStart.isEmpty())
                ? LocalDateTime.now()
                : DateMapper.toLocalDateTime(rangeStart);

        LocalDateTime end = (rangeEnd == null || rangeEnd.isEmpty())
                ? LocalDateTime.now().plusYears(5)
                : DateMapper.toLocalDateTime(rangeEnd);

        if (!start.isBefore(end)) {
            log.warn("Дата старта фильтра должна быть раньше даты окончания фильтра");
            throw new ValidationException("Дата старта фильтра должна быть раньше даты окончания фильтра");
        }

        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(appName)
                .uri(requestUri)
                .ip(userIp)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            EndpointHitDto endpointHitDto = statsClient.save(hitDto).getBody();
            log.info("Данные uri = {}, ip = {}, успешно добавлены в модуль статистики",
                    endpointHitDto.getUri(), endpointHitDto.getIp());
        } catch (Exception e) {
            log.warn("Статистика недоступна: {}", e.getMessage());
        }

        List<EventEntity> entityList = eventRepository.searchPublishedEvents(
                text == null ? null : text.toLowerCase(),
                categoryIdList,
                paid,
                start, end,
                onlyAvailable,
                pageRequest);

        List<EventShortDto> shortDtoList = entityList.stream()
                .map(EventMapper::toShortDto)
                .map(this::addDtoWithConfirmedRequestsAndViews)
                .collect(Collectors.toList());

        if (sort != null) {
            switch (SortValue.valueOf(sort)) {
                case EVENT_DATE:
                    shortDtoList.sort(Comparator.comparing(EventShortDto::getEventDate));
                    break;

                case VIEWS:
                    shortDtoList.sort(Comparator.comparing(EventShortDto::getViews));
                    break;

                default:
                    throw new ValidationException("Параметр для сортировки задан не верный");
            }
        } else {
            shortDtoList.sort(Comparator.comparing(EventShortDto::getId));
        }

        log.info("Найден список в количестве {}", shortDtoList.size());
        return shortDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEvent(Long eventId, String userIp, String requestUri) {
        log.info("Запрос на событие с id = {}", eventId);

        EventEntity entity = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(() -> {
            log.warn("Событие с id = {} не найдено", eventId);
            return new NotFoundException("Событие на найдено");
        });

        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(appName)
                .uri(requestUri)
                .ip(userIp)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            EndpointHitDto endpointHitDto = statsClient.save(hitDto).getBody();
            log.info("Данные uri = {}, ip = {}, успешно добавлены в модуль статистики",
                    endpointHitDto.getUri(), endpointHitDto.getIp());
        } catch (Exception e) {
            log.warn("Статистика недоступна: {}", e.getMessage());
        }

        EventFullDto fullDto = addDtoWithConfirmedRequestsAndViews(EventMapper.toFullDto(entity));

        log.info("Событие с id = {} успешно найдено", fullDto.getId());
        return fullDto;
    }

    private EventFullDto addDtoWithConfirmedRequestsAndViews(EventFullDto fullDto) {
        log.info("Запрос на добавление одобренных заявок и количество просмотров для событий {}", fullDto);

        long confirmed = requestRepository.countByEvent_IdAndStatus(
                fullDto.getId(), EventState.CONFIRMED);
        fullDto.setConfirmedRequests(confirmed);

        List<String> uris = List.of("/events/" + fullDto.getId());

        LocalDateTime dateStr = LocalDateTime.now().minusYears(100);
        LocalDateTime dateEnd = LocalDateTime.now();

        List<ViewStatsDto> stats = statsClient.getStats(dateStr, dateEnd, uris, true).getBody();
        if (stats != null) {
            stats.forEach(s -> log.info("ViewStatsDto: {}", s));
        } else {
            log.warn("Список stats пустой (null)");
        }

        long views = stats == null
                ? 0L
                : stats.stream()
                .mapToLong(ViewStatsDto::getHits)
                .sum();

        fullDto.setViews(views);

        log.info("Одобренные заявки и просмотры были успешно добавлены к событию {}", fullDto);
        return fullDto;
    }

    private EventShortDto addDtoWithConfirmedRequestsAndViews(EventShortDto shortDto) {
        log.info("Запрос на добавление одобренных заявок и количество просмотров для событий {}", shortDto);

        long confirmed = requestRepository.countByEvent_IdAndStatus(
                shortDto.getId(), EventState.CONFIRMED);
        shortDto.setConfirmedRequests(confirmed);

        List<String> uris = List.of("/events/" + shortDto.getId());

        LocalDateTime dateStr = LocalDateTime.now().minusYears(100);
        LocalDateTime dateEnd = LocalDateTime.now();

        List<ViewStatsDto> stats = statsClient.getStats(dateStr, dateEnd, uris, false).getBody();

        long views = stats == null
                ? 0L
                : stats.stream()
                .mapToLong(ViewStatsDto::getHits)
                .sum();

        shortDto.setViews(views);

        log.info("Одобренные заявки и просмотры были успешно добавлены к событию {}", shortDto);
        return shortDto;
    }
}