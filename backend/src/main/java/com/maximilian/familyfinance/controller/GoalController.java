package com.maximilian.familyfinance.controller;

import com.maximilian.familyfinance.dto.ResponseWrapper;
import com.maximilian.familyfinance.dto.family.CreateGoalDTO;
import com.maximilian.familyfinance.dto.family.GoalDTO;
import com.maximilian.familyfinance.dto.family.UpdateGoalDTO;
import com.maximilian.familyfinance.entity.family.Goal;
import com.maximilian.familyfinance.mapper.family.GoalMapper;
import com.maximilian.familyfinance.service.GoalService;
import com.maximilian.familyfinance.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/{familyId}/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;
    private final GoalMapper goalMapper;

    @PostMapping
    public ResponseEntity<ResponseWrapper<GoalDTO>> create(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо равняться 1")
            @PathVariable long familyId,

            @Valid
            @RequestBody CreateGoalDTO dto
    ) {
        Goal createdGoal = goalService.create(familyId, dto, userService.getCurrentAuthorizedUser());

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Goal created successfully",
                goalMapper.toDTO(createdGoal)
        ));
    }

    @PatchMapping("/{goalId}")
    public ResponseEntity<ResponseWrapper<GoalDTO>> updateGoal(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо равняться 1")
            @PathVariable long familyId,

            @Valid @Min(value = 1, message = "Id цели должно быть больше, либо равняться 1")
            @PathVariable long goalId,

            @Valid
            @RequestBody UpdateGoalDTO dto
    ) {
        Goal updatedGoal = goalService.update(familyId, goalId, dto, userService.getCurrentAuthorizedUser());

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Goal updated successfully",
                goalMapper.toDTO(updatedGoal)
        ));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<?>> getAllGoalsByFamId(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо равняться 1")
            @PathVariable long familyId
    ) {
        List<Goal> goalsList = goalService.getAllGoalsByFamilyId(familyId, userService.getCurrentAuthorizedUser());

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Goals list fetched successfully",
                goalsList.stream()
                        .map(goalMapper::toDTO)
        ));
    }

//    TODO: Сделать удаление цели + безопасное удаление
}
