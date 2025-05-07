package dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class FamilyDTO {
    private Long id;
    private String name;
    private OffsetDateTime createdAt;
    private UserDTO owner;
    private List<FamilyMemberDTO> members;
    private List<BudgetDTO> budgets;
    private List<CategoryDTO> categories;
}