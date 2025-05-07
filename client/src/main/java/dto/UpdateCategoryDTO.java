package dto;

import lombok.Data;

@Data
public class UpdateCategoryDTO {
    private String name;

    public UpdateCategoryDTO(String name) {
        this.name = name;
    }
}