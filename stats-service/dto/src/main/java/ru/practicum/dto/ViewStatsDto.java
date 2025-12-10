package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
