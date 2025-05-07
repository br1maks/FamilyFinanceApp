package com.maximilian.familyfinance.mapper.family;

import com.maximilian.familyfinance.dto.family.CreateFamilyDTO;
import com.maximilian.familyfinance.dto.family.FamilyDTO;
import com.maximilian.familyfinance.entity.family.Family;
import com.maximilian.familyfinance.mapper.UserMapper;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {
                UserMapper.class,
                FamilyMemberMapper.class,
                BudgetMapper.class
        }
)
public interface FamilyMapper {

    FamilyDTO toFamilyDTO(Family family);
    Family toEntity(CreateFamilyDTO dto);
}
