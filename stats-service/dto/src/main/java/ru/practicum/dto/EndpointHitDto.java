package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {
    private Long id;

    @NotBlank(message = "Идентификатор сервиса не должен быть пустым")
    private String app;

    @NotBlank(message = "URI запроса не должен быть пустым")
    private String uri;

    @NotBlank(message = "IP-адрес пользователя не должен быть пустым")
    private String ip;

    @NotNull(message = "Дата создания статистики не должна быть null")
    private LocalDateTime timestamp;
}
