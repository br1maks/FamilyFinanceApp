package com.maximilian.familyfinance.dto.family;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteCategoryDTO {

    @NotNull(message = "Id семьи не должно быть null")
    @Min(value = 1, message = "Id семьи должно быть больше, либо ровняться 1")
    private long familyId;
}
