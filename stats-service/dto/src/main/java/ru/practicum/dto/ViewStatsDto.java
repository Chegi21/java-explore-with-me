package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ViewStatsDto {
    String app;
    String uri;
    Long hits;
}
