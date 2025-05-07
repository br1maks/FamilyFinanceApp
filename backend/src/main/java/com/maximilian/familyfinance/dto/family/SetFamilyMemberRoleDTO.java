package com.maximilian.familyfinance.dto.family;

import com.maximilian.familyfinance.enums.family.FamilyMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetFamilyMemberRoleDTO {

    @NotNull(message = "Роль не может быть null")
    private FamilyMemberRole role;
}
