package com.maximilian.familyfinance.mapper.family;

import com.maximilian.familyfinance.dto.family.CreateGoalDTO;
import com.maximilian.familyfinance.dto.family.GoalDTO;
import com.maximilian.familyfinance.entity.family.Goal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    GoalDTO toDTO(Goal goal);

    Goal toEntity(CreateGoalDTO dto);
}
