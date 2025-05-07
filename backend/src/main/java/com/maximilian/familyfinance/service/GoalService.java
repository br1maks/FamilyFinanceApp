package com.maximilian.familyfinance.service;

import com.maximilian.familyfinance.dto.family.CreateGoalDTO;
import com.maximilian.familyfinance.dto.family.UpdateGoalDTO;
import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.entity.family.Family;
import com.maximilian.familyfinance.entity.family.Goal;
import com.maximilian.familyfinance.exception.family.FamilyNotFoundException;
import com.maximilian.familyfinance.exception.family.GoalNotFoundException;
import com.maximilian.familyfinance.mapper.family.GoalMapper;
import com.maximilian.familyfinance.repository.FamilyRepository;
import com.maximilian.familyfinance.repository.GoalRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final FamilyService familyService;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final FamilyRepository familyRepository;

    public List<Goal> getAllGoalsByFamilyId(long familyId, User currentUser) {
        if (!familyService.isFamilyMember(familyId, currentUser.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        return goalRepository.findAll();
    }

    public Goal create(long familyId, CreateGoalDTO dto, User currentUser) {
        if (!familyService.isFamilyMember(familyId, currentUser.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        Goal goal = goalMapper.toEntity(dto);
        goal.setFamily(family);

        return goalRepository.save(goal);
    }

    public Goal update(long familyId, long goalId, UpdateGoalDTO dto, User currentUser) {
        if (!familyService.isFamilyMember(familyId, currentUser.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException(goalId));

        if (dto.getGoalAmount() != null && dto.getGoalAmount().compareTo(BigDecimal.ZERO) < 0) {
            goal.setGoalAmount(dto.getGoalAmount());
        }

        Optional.ofNullable(dto.getName())
                .ifPresent(goal::setName);

        return goalRepository.save(goal);
    }
}
