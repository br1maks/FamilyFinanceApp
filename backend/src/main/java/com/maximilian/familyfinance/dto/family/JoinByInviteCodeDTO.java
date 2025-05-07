package com.maximilian.familyfinance.dto.family;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JoinByInviteCodeDTO {

    @NotNull(message = "Код приглашения не может быть null") @NotBlank(message = "Код приглашения не может быть пустым")
    private String inviteCode;
}
