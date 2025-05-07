package com.maximilian.familyfinance.mapper.family;

import com.maximilian.familyfinance.dto.family.TransactionDTO;
import com.maximilian.familyfinance.entity.family.Transaction;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {
                CategoryMapper.class
        }
)
public interface TransactionMapper {
    TransactionDTO toDTO(Transaction transaction);
}
