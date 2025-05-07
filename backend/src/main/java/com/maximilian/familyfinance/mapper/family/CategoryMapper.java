package com.maximilian.familyfinance.mapper.family;

import com.maximilian.familyfinance.dto.family.CategoryDTO;
import com.maximilian.familyfinance.entity.family.Category;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {
                FamilyMapper.class
        }
)
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);
}
