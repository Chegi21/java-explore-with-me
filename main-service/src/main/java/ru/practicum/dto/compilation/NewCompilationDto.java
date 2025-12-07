package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    @NotBlank(message = "Название подборки не может быть пустым")
    @Size(min = 1, max = 50)
    String title;

    @NotNull(message = "Список событий не может быть null")
    @Size(min = 1, message = "Список событий должен содержать хотя бы один элемент")
    Set<Long> events;

    Boolean pinned = false;
}

