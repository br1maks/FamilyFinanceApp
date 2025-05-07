package com.maximilian.familyfinance.controller;

import com.maximilian.familyfinance.dto.ResponseWrapper;
import com.maximilian.familyfinance.dto.family.CreateTransactionDTO;
import com.maximilian.familyfinance.dto.family.TransactionDTO;
import com.maximilian.familyfinance.entity.family.Transaction;
import com.maximilian.familyfinance.mapper.family.TransactionMapper;
import com.maximilian.familyfinance.service.TransactionService;
import com.maximilian.familyfinance.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final TransactionMapper transactionMapper;

    @GetMapping
    public ResponseEntity<ResponseWrapper<?>> getAllTransactionsByPeriod(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо равняться 1")
            @RequestParam long familyId,

            @RequestParam YearMonth period
    ) {
        List<Transaction> transactions = transactionService.getAllByFamilyIdAndPeriodIfMember(
                familyId,
                period,
                userService.getCurrentAuthorizedUser()
        );

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Transactions fetched successfully",
                transactions.stream()
                        .map(transactionMapper::toDTO)
                        .toList()
        ));
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<TransactionDTO>> create(
            @Valid
            @RequestBody CreateTransactionDTO dto
    ) {
        Transaction createdTransaction = transactionService.create(
                dto.getFamilyId(),
                dto.getCategoryId() == null ? null : dto.getCategoryId(),
                dto.getGoalId(),
                dto.getAmount(),
                dto.getType(),
                userService.getCurrentAuthorizedUser()
        );

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Transaction created successfully",
                transactionMapper.toDTO(createdTransaction)
        ));
    }
}

