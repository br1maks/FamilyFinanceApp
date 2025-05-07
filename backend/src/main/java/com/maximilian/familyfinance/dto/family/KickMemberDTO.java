package com.maximilian.familyfinance.dto.family;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KickMemberDTO {

    @Min(value = 1, message = "Id пользователя должно быть больше, либо равно 1")
    @NotNull(message = "Id пользователя не может быть null")
    private long userId;
}
