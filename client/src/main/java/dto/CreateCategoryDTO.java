package dto;

import lombok.Data;

@Data
public class CreateCategoryDTO {
    private String name;
    private long familyId;
}