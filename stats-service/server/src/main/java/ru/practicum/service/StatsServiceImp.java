package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHitEntity;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StatsServiceImp implements StatsService {
    private final StatsRepository repository;

    @Autowired
    public StatsServiceImp(StatsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
       log.info("Запрос на список статистики в промежутке от {} до {} c list {}", start, end, uris);

       if (start.isAfter(end)) {
           log.warn("Дата начала не может быть после даты окончания");
           throw new BadRequestException("start date is after end date");
       }

        List<ViewStatsDto> list;
        if (uris == null || uris.isEmpty()) {
            list = unique
                    ? repository.getUniqueViewStat(start, end)
                    : repository.getViewStat(start, end);
        } else {
            list = unique
                    ? repository.getListUniqueViewStats(start, end, uris)
                    : repository.getListViewStats(start, end, uris);
        }

        log.info("Найден список в количестве {} c uri {}", list.size(), list);
        return list;
    }

    @Transactional()
    @Override
    public EndpointHitDto save(EndpointHitDto dto) {
        log.info("Запрос на создание статистики для сервиса {} с ссылкой {}", dto.getApp(), dto.getUri());

        EndpointHitEntity created = repository.save(StatsMapper.toEndpointHitEntity(dto));

        log.info("Статистика для сервиса {} с ссылкой {} успешно создана", dto.getApp(), dto.getUri());
        return StatsMapper.toEndpointHitDto(created);
    }
}
