package dto;

import enums.FamilyMemberRole;
import lombok.Data;

@Data
public class FamilyMemberDTO {
    private Long id;
    private UserDTO user;
    private FamilyMemberRole role;
}