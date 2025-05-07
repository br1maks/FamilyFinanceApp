package dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Data
public class BudgetDTO {
    private Long id;
    private BigDecimal budgetLimit;
    private YearMonth period;
    private BigDecimal amount;
    private List<TransactionDTO> transactions;
}