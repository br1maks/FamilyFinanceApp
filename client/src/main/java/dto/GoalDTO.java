package dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoalDTO {
    private long id;
    private String name;
    private BigDecimal goalAmount;
    private BigDecimal accumulatedAmount;
}