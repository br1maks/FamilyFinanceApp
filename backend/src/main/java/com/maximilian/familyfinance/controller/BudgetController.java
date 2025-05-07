package com.maximilian.familyfinance.controller;

import com.maximilian.familyfinance.dto.ResponseWrapper;
import com.maximilian.familyfinance.dto.family.BudgetDTO;
import com.maximilian.familyfinance.dto.family.BudgetLimitRequestDTO;
import com.maximilian.familyfinance.entity.family.Budget;
import com.maximilian.familyfinance.mapper.family.BudgetMapper;
import com.maximilian.familyfinance.service.BudgetService;
import com.maximilian.familyfinance.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final UserService userService;
    private final BudgetMapper budgetMapper;

    @GetMapping("/{familyId}")
    public ResponseEntity<ResponseWrapper<List<BudgetDTO>>> getBudgetById(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо равно 1")
            @PathVariable long familyId
    ) {
        List<Budget> foundBudgets = budgetService.getByFamilyIdIfFamilyMember(familyId, userService.getCurrentAuthorizedUser());
        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Budget fetched successfully",
                foundBudgets.stream()
                        .map(budgetMapper::toDTO)
                        .toList()
        ));
    }

    @PatchMapping("/{familyId}/limit")
    public ResponseEntity<ResponseWrapper<?>> setLimit(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо равно 1")
            @PathVariable long familyId,
            @Valid @RequestBody BudgetLimitRequestDTO dto
    ) {
        Budget updatedBudget = budgetService.setCurrentLimitByFamilyId(
                familyId,
                dto.getLimit(),
                userService.getCurrentAuthorizedUser()
        );

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Budget limit updated successfully",
                budgetMapper.toDTO(updatedBudget)
        ));
    }
}
