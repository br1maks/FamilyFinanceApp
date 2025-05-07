package com.maximilian.familyfinance.dto.family;

import com.maximilian.familyfinance.dto.UserDTO;
import com.maximilian.familyfinance.enums.family.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;
    private UserDTO createdBy;
    private OffsetDateTime createdAt;
    private BigDecimal amount;
    private TransactionType type;
    private CategoryDTO category;
}
