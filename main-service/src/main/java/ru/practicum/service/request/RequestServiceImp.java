package ru.practicum.service.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResponse;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.BadRequestException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.EventEntity;
import ru.practicum.model.RequestEntity;
import ru.practicum.model.UserEntity;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RequestServiceImp implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public RequestServiceImp(RequestRepository requestRepository,
                             EventRepository eventRepository,
                             UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsByCurrentUserOfCurrentEvent(Long userId, Long eventId) {
        log.info("Запрос на информацию о запросах на участие в событие с id = {} пользователем с id = {}", eventId, userId);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Указанный пользователь не найден");
        });

        EventEntity event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id = {} не найдено", eventId);
            return new NotFoundException("Указанное событие не найдено");
        });

        if (!event.getInitiator().getId().equals(user.getId())) {
            log.warn("Запросы может просматривать только инициатор события");
            throw new ForbiddenException("Запросы может просматривать только инициатор события");
        }

        List<RequestEntity> requestList = requestRepository.findAllByEvent_InitiatorIdAndEvent_Id(userId, eventId);

        List<ParticipationRequestDto> participationRequestDtoList = requestList.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();

        log.info("Найдено запросов в количестве {}", participationRequestDtoList.size());
        return participationRequestDtoList;
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResponse updateRequest(Long userId, Long eventId,
                                                          EventRequestStatusUpdateRequest eventRequest) {
        log.info("Запрос на обновление статуса заявок на участие в событие id = {} пользователя с id = {}", eventId, userId);

        EventEntity event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Указанное событие не найдено"));

        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Указанный пользователь не найден");
        }

        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("Инициатор с id = {} не соответствует пользователю с id = {}", event.getInitiator().getId(), userId);
            throw new ForbiddenException("Статусы запросов может менять только инициатор");
        }

        if (event.getParticipantLimit() == 0 && !event.getRequestModeration()) {
            log.warn("Модерация не требуется");
            throw new BadRequestException("Модерация не требуется");
        }

        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, EventState.CONFIRMED);
        long participantLimit = event.getParticipantLimit();

        List<RequestEntity> requests = requestRepository.findAllByIdIn(eventRequest.getRequestIds());
        if (requests.isEmpty()) {
            log.warn("Список запросов пустой");
            throw new NotFoundException("Запросы не найдены");
        }

        if (confirmedCount + requests.size() > event.getParticipantLimit()) {
            log.warn("Лимит заявок в количестве {} превышен", event.getParticipantLimit());
            throw new ConflictException("Лимит превышен");
        }

        EventState newStatus = eventRequest.getStatus();
        List<RequestEntity> confirmedRequests = new ArrayList<>();
        List<RequestEntity> rejectedRequests = new ArrayList<>();

        for (RequestEntity request : requests) {
            if (!request.getEvent().getId().equals(eventId)) {
                log.warn("Запрос с id = {] не соответствует событию с Id = {}", request.getId(), request.getEvent().getId());
                throw new ConflictException("Запрос должен принадлежать событию");
            }

            if (!request.getStatus().equals(EventState.PENDING)) {
                continue;
            }

            if (newStatus == EventState.CONFIRMED) {
                if (confirmedCount >= participantLimit) {
                    request.setStatus(EventState.REJECTED);
                    rejectedRequests.add(request);
                    continue;
                }

                request.setStatus(EventState.CONFIRMED);
                confirmedRequests.add(request);
                confirmedCount++;

            } else if (newStatus == EventState.REJECTED) {
                request.setStatus(EventState.REJECTED);
                rejectedRequests.add(request);
            }
        }

        event.setConfirmedRequests(confirmedCount);
        eventRepository.save(event);

        requestRepository.saveAll(requests);

        log.info("Заявки подтверждены: {}, отклонены: {}", confirmedRequests.size(), rejectedRequests.size());
        return new EventRequestStatusUpdateResponse(
                confirmedRequests.stream().map(RequestMapper::toParticipationRequestDto).toList(),
                rejectedRequests.stream().map(RequestMapper::toParticipationRequestDto).toList()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsByCurrentUser(Long userId) {
        log.info("Запрос списка заявок на участие в событиях от пользователя с id = {}", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        List<RequestEntity> requestList = requestRepository.findAllByRequesterIdAndNotInitiator(userId);

        log.info("Список заявок найден в количестве {}", requestList.size());
        return requestList.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();

    }

    @Transactional
    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        log.info("Запрос на добавление запроса на участие в событие с id = {} от пользователя с id = {}", eventId, userId);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Указанный пользователь не найден");
        });

        EventEntity event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id = {} не найдено", eventId);
            return new NotFoundException("Указанное событие не найдено");
        });

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            log.warn("Запрос пользователем с id = {} уже создан", userId);
            throw new ConflictException("Запрос уже создан");
        }

        if (event.getInitiator().getId().equals(userId)) {
            log.warn("Пользователь с id = {} является инициатором события с id = {}", userId, eventId);
            throw new ConflictException("Пользователь является инициатором события");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.warn("Событие должно иметь статус {}", EventState.PUBLISHED);
            throw new ConflictException("Событие должно иметь статусом PUBLISHED");
        }

        Long limit = event.getParticipantLimit();
        if (limit > 0 && limit <= requestRepository.countByEventIdAndStatus(event.getId(), EventState.CONFIRMED)) {
            log.warn("Достигнуто максимальное количество участников");
            throw new ConflictException("Достигнуто максимальное количество участников");
        }

        RequestEntity request = new RequestEntity();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(event.getRequestModeration() ? EventState.PENDING : EventState.CONFIRMED);

        if (limit == 0) {
            request.setStatus(EventState.CONFIRMED);
        }

        if (request.getStatus() == EventState.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        RequestEntity createRequest = requestRepository.save(request);

        log.info("Запрос с id={} успешно создан", createRequest.getId());
        return RequestMapper.toParticipationRequestDto(createRequest);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Запрос на отмену запроса с id = {} на участие в событие от пользователя с id = {}", requestId, userId);

        RequestEntity request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() -> {
            log.warn("Запрос с id = {} не найден", requestId);
            return new NotFoundException("Запрос не найден");
        });

        if (!request.getRequester().getId().equals(userId)) {
            log.warn("Пользователь с id = {} не соответствует инициатору события с id = {}", requestId, userId);
            throw new ForbiddenException("Отменять можно только свой запрос");
        }

        if (request.getStatus() == EventState.CANCELED) {
            log.warn("Запрос с id = {} уже отменен", requestId);
            throw new ConflictException("Запрос уже отменен");
        }

        if (request.getStatus() == EventState.CONFIRMED) {
            EventEntity event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }

        request.setStatus(EventState.CANCELED);

        RequestEntity updateRequest = requestRepository.save(request);

        log.info("Запрос с id = {} успешно отменен", updateRequest.getId());
        return RequestMapper.toParticipationRequestDto(updateRequest);
    }
}