package com.maximilian.familyfinance.mapper.family;

import com.maximilian.familyfinance.dto.family.BudgetDTO;
import com.maximilian.familyfinance.entity.family.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { TransactionMapper.class })
public interface BudgetMapper {

    @Mapping(target = "transactions", source = "transactions")
    BudgetDTO toDTO(Budget budget);
}
