package dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateGoalDTO {
    private String name;
    private BigDecimal goalAmount;
}