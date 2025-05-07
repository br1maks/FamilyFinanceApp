package com.maximilian.familyfinance.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {

    @Size(min = 3, max = 48, message = "Имя пользователя должно содержать от 3 до 48 символов")
    @NotNull(message = "Имя пользователя не может быть пустым")
    @NotBlank(message = "Имя пользователя не может быть пустым или содержать только пробелы")
    private String username;

    @Size(min = 8, max = 128, message = "Пароль должен содержать от 8 до 128 символов")
    @NotNull(message = "Пароль не может быть пустым")
    @NotBlank(message = "Пароль не может быть пустым или содержать только пробелы")
    private String password;
}
