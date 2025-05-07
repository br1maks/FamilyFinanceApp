package com.maximilian.familyfinance.dto.family;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateFamilyDTO {

    @NotNull(message = "Имя не может быть null")
    @NotBlank(message = "Имя не может быть пустым")
    @Pattern(regexp = "^[\\p{L}\\d]+(?:[ -][\\p{L}\\d]+)*$", message = "Имя может содержать только буквы, цифры, пробел или дефис")
    private String name;
}
