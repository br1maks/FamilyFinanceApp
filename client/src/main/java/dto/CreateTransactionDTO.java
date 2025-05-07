package dto;

import enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionDTO {
    private BigDecimal amount;
    private TransactionType type;
    private Long categoryId;
    private Long goalId;
    private long familyId;
}