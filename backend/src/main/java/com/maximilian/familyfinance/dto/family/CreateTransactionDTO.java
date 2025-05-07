package com.maximilian.familyfinance.dto.family;

import com.maximilian.familyfinance.enums.family.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionDTO {

    @NotNull(message = "Цена не должна быть пустой")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0.01")
    private BigDecimal amount;

    @NotNull(message = "Тип транзакции не может быть null")
    private TransactionType type;

    @Min(value = 1, message = "Id категории должно быть больше, либо равняться 1")
    private Long categoryId;

    @Min(value = 1, message = "Id семьи должно быть больше, либо равняться 1")
    private long familyId;

    @Min(value = 1, message = "Id цели должно быть больше, либо равняться 1")
    private Long goalId;
}
