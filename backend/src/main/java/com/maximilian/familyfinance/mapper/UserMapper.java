package com.maximilian.familyfinance.mapper;

import com.maximilian.familyfinance.dto.auth.RegisterDTO;
import com.maximilian.familyfinance.dto.UserDTO;
import com.maximilian.familyfinance.entity.User;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface UserMapper {
    User toUser(RegisterDTO dto);
    UserDTO toDTO(User user);
}
