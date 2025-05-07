package com.maximilian.familyfinance.service;

import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.entity.family.Budget;
import com.maximilian.familyfinance.entity.family.Family;
import com.maximilian.familyfinance.entity.family.FamilyMember;
import com.maximilian.familyfinance.enums.family.FamilyMemberRole;
import com.maximilian.familyfinance.exception.budget.BudgetNotFoundException;
import com.maximilian.familyfinance.exception.family.FamilyMemberNotFoundException;
import com.maximilian.familyfinance.exception.family.FamilyNotFoundException;
import com.maximilian.familyfinance.mapper.family.BudgetMapper;
import com.maximilian.familyfinance.repository.BudgetRepository;
import com.maximilian.familyfinance.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static com.maximilian.familyfinance.enums.family.FamilyMemberRole.ROLE_MANAGER;
import static com.maximilian.familyfinance.enums.family.FamilyMemberRole.ROLE_OWNER;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final FamilyRepository familyRepository;
    private final BudgetMapper budgetMapper;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void createBudgetsForNewMonth() {
        YearMonth currentYearMonth = YearMonth.now();

        for (Family family : familyRepository.findAll()) {
            getOrCreateBudget(family, currentYearMonth);
        }
    }

    public Budget getOrCreateBudget(Family family) {
        return getOrCreateBudget(family, YearMonth.now());
    }

    public Budget getOrCreateBudget(Family family, YearMonth yearMonth) {
        return family.getBudgets().stream()
                .filter(budget -> budget.getPeriod().equals(yearMonth))
                .findFirst()
                .orElseGet(() -> {
                    Budget createdBudget = budgetRepository.save(Budget.builder()
                            .family(family)
                            .build()
                    );
                    family.getBudgets().add(createdBudget);
                    familyRepository.save(family);
                    return createdBudget;
                });
    }

    public List<Budget> getByFamilyIdIfFamilyMember(long familyId, User currentUser) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Family not found"));

        if (!isFamilyMember(family, currentUser)) {
            throw new AccessDeniedException("Access denied");
        }

        return family.getBudgets();
    }

    private boolean isFamilyMember(Family family, User user) {
        return familyRepository.existsByIdAndMembers_User_Id(family.getId(), user.getId());
    }

    public Budget setCurrentLimitByFamilyId(long familyId, BigDecimal budgetLimit, User currentAuthorizedUser) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Family not found"));

        FamilyMember currentFamilyMember = family.getMembers().stream()
                .filter(member -> member.getUser().equals(currentAuthorizedUser))
                .findFirst()
                .orElseThrow(() -> new FamilyMemberNotFoundException("Family member not found exception"));

        if (currentFamilyMember.getRole() != ROLE_OWNER && currentFamilyMember.getRole() != ROLE_MANAGER) {
            throw new AccessDeniedException("Access denied");
        }

        Budget budget = getOrCreateBudget(family);
        budget.setBudgetLimit(budgetLimit);

        return budgetRepository.save(budget);
    }
}
