package com.maximilian.familyfinance.service;

import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.entity.family.Category;
import com.maximilian.familyfinance.entity.family.Family;
import com.maximilian.familyfinance.exception.family.CategoryNotFoundException;
import com.maximilian.familyfinance.exception.family.FamilyNotFoundException;
import com.maximilian.familyfinance.exception.family.NotOwnCategoryException;
import com.maximilian.familyfinance.repository.CategoryRepository;
import com.maximilian.familyfinance.repository.FamilyRepository;
import com.maximilian.familyfinance.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final FamilyService familyService;
    private final FamilyRepository familyRepository;

    private final CategoryRepository categoryRepository;

    private final TransactionRepository transactionRepository;

    public List<Category> findAllByFamilyIdIfMember(long familyId, User currentUser) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Family with id '%d' not found".formatted(familyId)));

        if (!familyService.isFamilyMember(family, currentUser)) {
            throw new AccessDeniedException("Access denied");
        }

        return family.getCategories();
    }

    public Category create(String name, long familyId, User currentUser) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Family with id '%d' not found".formatted(familyId)));

        if (!familyService.isFamilyMember(family, currentUser)) {
            throw new AccessDeniedException("Access denied");
        }

        Category createdCategory = Category.builder()
                .family(family)
                .name(name)
                .createdBy(currentUser)
                .build();

        return categoryRepository.save(createdCategory);
    }

    @Transactional
    public void delete(long familyId, long categoryId, User currentUser) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Family with id '%d' not found".formatted(familyId)));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if (!familyService.isFamilyMember(family, currentUser)) {
            throw new AccessDeniedException("Access denied");
        }

        if (!category.getCreatedBy().equals(currentUser)) {
            throw new NotOwnCategoryException("Only owner of this category can delete it");
        }

        transactionRepository.unassignCategoryFromTransactions(category);

        categoryRepository.delete(category);
    }
}
