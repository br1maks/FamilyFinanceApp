package com.maximilian.familyfinance.dto.family;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateGoalDTO {

    @NotBlank(message = "Name can not be blank")
    private String name;
    private BigDecimal goalAmount;
}
