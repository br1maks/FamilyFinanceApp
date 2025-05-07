package com.maximilian.familyfinance.dto.family;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateGoalDTO {

    @NotBlank(message = "Name can not be blank")
    @NotNull(message = "Name can not be null")
    private String name;

    @NotNull(message = "Goal amount can not be null")
    private BigDecimal goalAmount;
}
