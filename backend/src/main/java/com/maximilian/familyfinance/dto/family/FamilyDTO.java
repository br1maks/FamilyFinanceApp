package com.maximilian.familyfinance.dto.family;

import com.maximilian.familyfinance.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FamilyDTO {

    private Long id;
    private String name;
    private OffsetDateTime createdAt;
    private UserDTO owner;
    private List<FamilyMemberDTO> members;
    private List<BudgetDTO> budgets;
    private List<CategoryDTO> categories;
}
