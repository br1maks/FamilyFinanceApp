package com.maximilian.familyfinance.service;

import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.entity.family.*;
import com.maximilian.familyfinance.enums.family.TransactionType;
import com.maximilian.familyfinance.exception.budget.BudgetNotFoundException;
import com.maximilian.familyfinance.exception.family.CategoryNotFoundException;
import com.maximilian.familyfinance.exception.family.FamilyNotFoundException;
import com.maximilian.familyfinance.exception.family.GoalNotFoundException;
import com.maximilian.familyfinance.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BudgetService budgetService;
    private final FamilyService familyService;
    private final TransactionRepository transactionRepository;
    private final FamilyRepository familyRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final GoalRepository goalRepository;

    public List<Transaction> getAllByFamilyIdAndPeriodIfMember(long familyId, YearMonth period, User currentUser) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Family with id '%d' not found".formatted(familyId)));

        if (!familyService.isFamilyMember(family, currentUser)) {
            throw new AccessDeniedException("Access denied");
        }

        Budget budget = budgetService.getOrCreateBudget(family, period);
        return budget.getTransactions();
    }

    @Transactional
    public Transaction create(long familyId, Long categoryId, Long goalId, BigDecimal amount, TransactionType type, User currentUser) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Family with id '%d' not found".formatted(familyId)));

        if (!familyService.isFamilyMember(family, currentUser)) {
            throw new AccessDeniedException("Access denied");
        }

        Category category = null;

        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        }

        Goal goal = null;

        if (goalId != null) {
            goal = goalRepository.findById(goalId)
                    .orElseThrow(() -> new GoalNotFoundException(goalId));

            goal.setAccumulatedAmount(goal.getAccumulatedAmount().add(amount));
        }

        Budget budget = budgetService.getOrCreateBudget(family);
        Transaction transaction = transactionRepository.save(Transaction.builder()
                .category(category)
                .goal(goal)
                .createdBy(currentUser)
                .budget(budget)
                .amount(amount)
                .type(type)
                .build()
        );

        if (type == TransactionType.INCOME) {
            budget.setAmount(budget.getAmount().add(amount));
        } else if (type == TransactionType.EXPENSE) {
            budget.setAmount(budget.getAmount().subtract(amount));
        }

        budget.getTransactions().add(transaction);
        budgetRepository.save(budget);

        return transaction;
    }
}
