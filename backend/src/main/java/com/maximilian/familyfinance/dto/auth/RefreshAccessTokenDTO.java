package com.maximilian.familyfinance.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshAccessTokenDTO {

    @NotNull(message = "Refresh token can't be null")
    private String refreshToken;
}
