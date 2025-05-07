package com.maximilian.familyfinance.repository;

import com.maximilian.familyfinance.entity.family.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
}
