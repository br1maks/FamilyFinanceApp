package dto;

import enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class TransactionDTO {
    private Long id;
    private UserDTO createdBy;
    private OffsetDateTime createdAt;
    private BigDecimal amount;
    private TransactionType type;
    private CategoryDTO category;
}