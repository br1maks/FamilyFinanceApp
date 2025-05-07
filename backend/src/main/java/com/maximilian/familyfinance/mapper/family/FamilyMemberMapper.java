package com.maximilian.familyfinance.mapper.family;

import com.maximilian.familyfinance.dto.family.FamilyMemberDTO;
import com.maximilian.familyfinance.entity.family.FamilyMember;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FamilyMemberMapper {

    FamilyMemberDTO toDTO(FamilyMember familyMember);
}
