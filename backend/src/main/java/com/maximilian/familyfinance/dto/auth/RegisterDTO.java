package com.maximilian.familyfinance.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {

    @Size(min = 3, max = 48, message = "Имя пользователя должно содержать от 3 до 48 символов")
    @NotNull(message = "Имя пользователя не может быть пустым")
    @NotBlank(message = "Имя пользователя не может быть пустым или содержать только пробелы")
    @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Имя пользователя может содержать только латинские буквы, цифры, точки и подчеркивания")
    private String username;

    @Size(min = 1, max = 64, message = "Имя должно содержать от 1 до 64 символов")
    @NotNull(message = "Имя не может быть пустым")
    @NotBlank(message = "Имя не может быть пустым или содержать только пробелы")
    @Pattern(regexp = "^\\p{L}+$", message = "Имя должно содержать только буквы")
    private String firstName;

    @Size(min = 1, max = 64, message = "Фамилия должна содержать от 1 до 64 символов")
    @NotNull(message = "Фамилия не может быть пустой")
    @NotBlank(message = "Фамилия не может быть пустой или содержать только пробелы")
    @Pattern(regexp = "^\\p{L}+$", message = "Фамилия должна содержать только буквы")
    private String lastName;

    @Size(min = 8, max = 128, message = "Пароль должен содержать от 8 до 128 символов")
    @NotNull(message = "Пароль не может быть пустым")
    @NotBlank(message = "Пароль не может быть пустым или содержать только пробелы")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[_#?!@$%^&*-]).+$",
            message = "Пароль должен содержать хотя бы одну заглавную букву, одну строчную букву, одну цифру и один специальный символ")
    private String password;
}