package com.maximilian.familyfinance.repository;

import com.maximilian.familyfinance.entity.family.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface familyMemberRepository extends JpaRepository<FamilyMember, Long> {
    Optional<FamilyMember> findFamilyMemberByUser_Id(long userId);
}
