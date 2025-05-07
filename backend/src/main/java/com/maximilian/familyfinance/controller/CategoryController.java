package com.maximilian.familyfinance.controller;

import com.maximilian.familyfinance.dto.ResponseWrapper;
import com.maximilian.familyfinance.dto.family.CategoryDTO;
import com.maximilian.familyfinance.dto.family.CreateCategoryDTO;
import com.maximilian.familyfinance.dto.family.DeleteCategoryDTO;
import com.maximilian.familyfinance.entity.family.Category;
import com.maximilian.familyfinance.mapper.family.CategoryMapper;
import com.maximilian.familyfinance.service.CategoryService;
import com.maximilian.familyfinance.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<CategoryDTO>>> getAllCategoriesByFamilyId(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо ровняться 1")
            @RequestParam long familyId
    ) {
        List<Category> categories = categoryService.findAllByFamilyIdIfMember(familyId, userService.getCurrentAuthorizedUser());

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Categories fetched successfully",
                categories.stream()
                        .map(categoryMapper::toDTO)
                        .toList()
        ));
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<CategoryDTO>> create(@Valid @RequestBody CreateCategoryDTO dto) {
        Category createdCategory = categoryService.create(dto.getName(), dto.getFamilyId(), userService.getCurrentAuthorizedUser());

        return ResponseEntity.created(URI.create("/api/v1/categories/%d".formatted(createdCategory.getId())))
                .body(ResponseWrapper.success(
                        HttpStatus.CREATED,
                        "Category created successfully",
                        categoryMapper.toDTO(createdCategory)
                ));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteCategory(
            @Valid
            @RequestBody DeleteCategoryDTO dto,

            @Valid@Min(value = 1, message = "Id семьи должно быть больше, либо ровняться 1")
            @PathVariable long categoryId
    ) {
        categoryService.delete(dto.getFamilyId(), categoryId, userService.getCurrentAuthorizedUser());

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Category deleted successfully"
        ));
    }
}
