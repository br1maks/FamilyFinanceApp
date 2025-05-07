package com.maximilian.familyfinance.repository;

import com.maximilian.familyfinance.entity.family.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
