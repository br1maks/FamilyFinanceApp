package com.maximilian.familyfinance.repository;

import com.maximilian.familyfinance.entity.family.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {

    List<Family> findByMembers_User_Id(Long membersUserId);
    Optional<Family> findByInviteCode(String inviteCode);
//    boolean existsByMembers_User_Id(Long userId);
    boolean existsByIdAndMembers_User_Id(Long familyId, Long userId);
//    boolean existsByIdAndMembers_User_Id(Long familyId, Long userId);
}
