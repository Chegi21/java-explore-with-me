package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 2, max = 250)
    String name;

    @NotBlank(message = "Данные почты не могут быть пустыми")
    @Email(message = "Не корректные данные почты")
    @Size(min = 6, max = 254)
    String email;
}
