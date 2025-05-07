package com.maximilian.familyfinance.dto.family;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetLimitRequestDTO {

    @NotNull(message = "Лимит бюджета не может быть null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Лимит бюджета должен быть больше 0")
    private BigDecimal limit;
}
