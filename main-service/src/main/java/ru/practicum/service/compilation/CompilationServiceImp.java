package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.CompilationEntity;
import ru.practicum.model.EventEntity;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImp implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilationList(Boolean pinned, Pageable pageable) {
        log.info("Запрос на список подборок (pinned = {})", pinned);

        Page<CompilationEntity> compilationPage =
                pinned != null
                        ? compilationRepository.findAllByPinnedOrderByIdDesc(pinned, pageable)
                        : compilationRepository.findAll(pageable);

        List<CompilationDto> dtoList = compilationPage.getContent().stream()
                .map(CompilationMapper::toDto)
                .map(this::addConfirmedRequestsAndViews)
                .toList();

        log.info("Найден список подборок: {} шт.", dtoList.size());
        return dtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilation(Long compilationId) {
        log.info("Запрос на подборку с id = {}", compilationId);

        CompilationEntity findEntity = compilationRepository.findById(compilationId).orElseThrow(() -> {
            log.warn("Подборка с id = {} не найдена", compilationId);
            return new NotFoundException("Подборка не найдена");
        });

        CompilationDto findDto = addConfirmedRequestsAndViews(CompilationMapper.toDto(findEntity));

        log.info("Подборка с id = {} успешна найдена", findDto.getId());
        return findDto;
    }

    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        log.info("Запрос на создание подборки с названием {}", newCompilationDto.getTitle());

        Set<EventEntity> events = new HashSet<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            Set<Long> eventIdList = newCompilationDto.getEvents();
            events = eventRepository.findAllByIdIn(eventIdList);
        }

        CompilationEntity newCompilationEntity = CompilationMapper.toEntity(newCompilationDto, events);

        CompilationEntity saveCompilationEntity = compilationRepository.save(newCompilationEntity);

        CompilationDto dto;
        if (!events.isEmpty()) {
            dto = addConfirmedRequestsAndViews(CompilationMapper.toDto(saveCompilationEntity));
        } else {
            dto = CompilationMapper.toDto(saveCompilationEntity);
        }

        log.info("Новая подборка с id = {} и названием {} успешна создана", dto.getId(), dto.getTitle());
        return dto;
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compilationId) {
        log.info("Запрос на удаление подборки с id = {}", compilationId);

        CompilationEntity findEntity = compilationRepository.findById(compilationId).orElseThrow(() -> {
            log.warn("Подборка с id = {} не найдена", compilationId);
            return new NotFoundException("Подборка не найдена");
        });

        compilationRepository.deleteById(compilationId);
        log.info("Подборка с id = {} успешна удалена", findEntity.getId());
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Запрос на обновление подборки с id = {}", compilationId);

        CompilationEntity findEntity = compilationRepository.findById(compilationId)
                .orElseThrow(() -> {
                    log.warn("Подборка с id = {} не найдена", compilationId);
                    return new NotFoundException("Подборка не найдена");
                });

        if (updateCompilationRequest.getEvents() != null) {
            if (!updateCompilationRequest.getEvents().isEmpty()) {
                Set<Long> eventIdList = updateCompilationRequest.getEvents();
                Set<EventEntity> events = eventRepository.findAllByIdIn(eventIdList);
                findEntity.setEvents(events);
            } else {
                findEntity.setEvents(new HashSet<>());
            }
        }

        if (updateCompilationRequest.getPinned() != null) {
            findEntity.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getTitle() != null) {
            findEntity.setTitle(updateCompilationRequest.getTitle());
        }

        CompilationEntity updatedEntity = compilationRepository.save(findEntity);

        CompilationDto dto;
        if (!updatedEntity.getEvents().isEmpty()) {
            dto = addConfirmedRequestsAndViews(CompilationMapper.toDto(updatedEntity));
        } else {
            dto = CompilationMapper.toDto(updatedEntity);
        }

        log.info("Подборка с id = {} успешно обновлена", dto.getId());
        return dto;
    }

    private CompilationDto addConfirmedRequestsAndViews(CompilationDto compilationDto) {
        log.info("Запрос на добавление одобренных заявок и количество просмотров для подборки с id = {}", compilationDto.getId());

        for (EventShortDto eventDto : compilationDto.getEvents()) {
            long confirmed = requestRepository.countByEvent_IdAndStatus(
                    eventDto.getId(), EventState.CONFIRMED);
            eventDto.setConfirmedRequests(confirmed);

            List<String> uris = List.of("/events/" + eventDto.getId());

            LocalDateTime dateStr = LocalDateTime.now().minusYears(100);
            LocalDateTime dateEnd = LocalDateTime.now();

            List<ViewStatsDto> statsList = statsClient
                    .getStats(dateStr, dateEnd, uris, true)
                    .getBody();

            long views = Optional.ofNullable(statsList)
                    .orElse(Collections.emptyList())
                    .stream()
                    .mapToLong(ViewStatsDto::getHits)
                    .sum();

            eventDto.setViews(views);

            log.info("Найдено одобренных заявок в количестве {} и просмотров в количестве {}", confirmed, views);
        }
        return compilationDto;
    }
}
