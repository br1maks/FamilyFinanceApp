package com.maximilian.familyfinance.repository;

import com.maximilian.familyfinance.entity.family.Category;
import com.maximilian.familyfinance.entity.family.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Modifying
    @Query("UPDATE Transaction t SET t.category = NULL WHERE t.category = :category")
    void unassignCategoryFromTransactions(@Param("category") Category category);
}
