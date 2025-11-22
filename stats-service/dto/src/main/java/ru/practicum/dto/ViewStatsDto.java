package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
