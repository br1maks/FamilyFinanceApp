package dto;

import enums.FamilyMemberRole;
import lombok.Data;

@Data
public class SetFamilyMemberRoleDTO {
    private FamilyMemberRole role;
}