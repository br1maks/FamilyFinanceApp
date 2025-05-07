package com.maximilian.familyfinance.dto.family;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetDTO {

    private Long id;
    private BigDecimal budgetLimit;
    private YearMonth period;
    private BigDecimal amount;
    private List<TransactionDTO> transactions;
}
