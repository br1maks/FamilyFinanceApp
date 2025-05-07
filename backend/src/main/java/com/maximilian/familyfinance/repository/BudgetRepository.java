package com.maximilian.familyfinance.repository;

import com.maximilian.familyfinance.entity.family.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByPeriod(YearMonth period);
}
