package dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetLimitRequestDTO {
    private BigDecimal limit;
}