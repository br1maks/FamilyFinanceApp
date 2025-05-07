package dto;

import enums.UserRole;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private UserRole role;
    private OffsetDateTime createdAt;
    private Boolean isAccountNonLocked;
}