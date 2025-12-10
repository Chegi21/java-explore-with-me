package ru.practicum.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.dto.location.LocationDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank(message = "Заголовок не должна быть пустой")
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank(message = "Аннотация не должна быть пустой")
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotBlank(message = "Описание не должно быть пустой")
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull(message = "Список категорий не должен быть пустым")
    private Long category;

    @NotNull(message = "Время создания события не должно быть null")
    private LocalDateTime eventDate;

    @NotNull(message = "Координаты не должна быть null")
    private LocationDto location;

    @Builder.Default
    private Boolean paid = false;

    @Builder.Default
    @PositiveOrZero(message = "Лимит участников должен быть положительным число")
    private Long participantLimit = 0L;

    @Builder.Default
    private Boolean requestModeration = true;
}

