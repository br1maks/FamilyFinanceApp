package dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateGoalDTO {
    private String name;
    private BigDecimal goalAmount;
}