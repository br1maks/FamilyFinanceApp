package com.maximilian.familyfinance.dto.family;

import com.maximilian.familyfinance.dto.UserDTO;
import com.maximilian.familyfinance.enums.family.FamilyMemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FamilyMemberDTO {

    private Long id;
    private UserDTO user;
    private FamilyMemberRole role;
}
