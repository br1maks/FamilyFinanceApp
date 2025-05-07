package com.maximilian.familyfinance.service;

import com.maximilian.familyfinance.dto.family.CreateFamilyDTO;
import com.maximilian.familyfinance.dto.family.UpdateFamilyDTO;
import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.entity.family.*;
import com.maximilian.familyfinance.enums.family.FamilyMemberRole;
import com.maximilian.familyfinance.exception.family.*;
import com.maximilian.familyfinance.mapper.family.FamilyMapper;
import com.maximilian.familyfinance.repository.FamilyRepository;
import com.maximilian.familyfinance.repository.familyMemberRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.maximilian.familyfinance.enums.family.FamilyMemberRole.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMapper familyMapper;
    private final familyMemberRepository familyMemberRepository;

    private final List<String> defaultCategoriesNames = List.of("Еда", "Транспорт", "Развлечения", "Коммунальные услуги");

    public List<Family> findFamiliesByMembersId(long id) {
        return familyRepository.findByMembers_User_Id(id);
    }

    public Family create(CreateFamilyDTO dto, User owner) {
        return create(familyMapper.toEntity(dto), owner);
    }

    private Family create(Family family, User owner) {
        family.setOwner(owner);

        FamilyMember familyMember = FamilyMember.builder()
                .family(family)
                .user(owner)
                .role(ROLE_OWNER)
                .build();
        family.getMembers().add(familyMember);

        Budget budget = Budget.builder()
                .family(family)
                .build();
        family.getBudgets().add(budget);

        for (String categoryName : defaultCategoriesNames) {
            family.getCategories().add(Category.builder()
                    .name(categoryName)
                    .family(family)
                    .createdBy(owner)
                    .build()
            );
        }

        return familyRepository.save(family);
    }

    public void deleteFamilyIfOwned(Long id, User user) {
        Family family = familyRepository.findById(id)
                .orElseThrow(() -> new FamilyNotFoundException("Family with id '%d' not found".formatted(id)));

        if (!family.getOwner().equals(user)) {
            throw new AccessDeniedException("Only the owner can delete family");
        }

        familyRepository.deleteById(id);
    }

    public String getFamilyInviteCodeIfOwner(long id, User user) {
        Family family = familyRepository.findById(id)
                .orElseThrow(() -> new FamilyNotFoundException("Family with id '%d' not found".formatted(id)));

        if (!family.getOwner().equals(user)) {
            throw new AccessDeniedException("Only the owner can get family invite code");
        }

        return family.getInviteCode();
    }

    public Family joinFamilyByInviteCode(String inviteCode, User user) {
        Family family = familyRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new InvalidInviteCodeException("Invalid invite code"));

        if (isFamilyMember(family, user)) {
            throw new FamilyAlreadyJoinedException("You are already joined");
        }

        FamilyMember member = FamilyMember.builder()
                .user(user)
                .family(family)
                .build();
        family.getMembers().add(member);

        return familyRepository.save(family);
    }

    public Family kickMemberIfOwnerOrManager(long familyId, long userId, User user) {
        if (!isFamilyMember(familyId, user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new InvalidInviteCodeException("Family not found"));

        FamilyMember familyMemberForKick = family.getMembers().stream()
                .filter(member -> member.getUser().getId() == userId)
                .findFirst()
                .orElseThrow(() -> new FamilyMemberNotFoundException("Family member not found"));

        FamilyMember currentFamilyMember = family.getMembers().stream()
                .filter(member -> member.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new FamilyMemberNotFoundException("Current family member not found"));

        if (!family.getOwner().equals(user) || currentFamilyMember.getRole() != ROLE_MANAGER) {
            throw new AccessDeniedException("Only owner or manager can kick members");
        }

        if (familyMemberForKick.getUser().equals(user)) {
            throw new CannotKickSelfException("You can't kick yourself");
        }

        family.getMembers().remove(familyMemberForKick);

        return familyRepository.save(family);
    }

    public Family getByIdIfMember(long familyId, User user) {
        if (!isFamilyMember(familyId, user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        return familyRepository.findById(familyId)
                .orElseThrow(() -> new InvalidInviteCodeException("Family not found"));
    }

    public void leave(long familyId, User user) {
        if (!isFamilyMember(familyId, user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new InvalidInviteCodeException("Family not found"));

        if (family.getOwner().equals(user)) {
            throw new OwnerCannotLeaveFamilyException("Owner cannot leave the family. It must be deleted instead");
        }

        FamilyMember familyMember = familyMemberRepository.findFamilyMemberByUser_Id(user.getId())
                .orElseThrow(() -> new FamilyMemberNotFoundException("Family member not found"));

        family.getMembers().remove(familyMember);
        familyRepository.save(family);
    }

    public FamilyMember SetFamilyMemberRole(long familyId, long userId, FamilyMemberRole role, User currentUser) {
        if (role == ROLE_OWNER) {
            throw new SingleOwnerException("A family can only have 1 owner");
        }

        if (!familyRepository.existsByIdAndMembers_User_Id(familyId, currentUser.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new InvalidInviteCodeException("Family not found"));

        if (!family.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("Only the owner can assign roles");
        }

        FamilyMember foundFamilyMember = family.getMembers().stream()
                .filter(member -> member.getUser().getId() == userId)
                .findFirst()
                .orElseThrow(() -> new FamilyMemberNotFoundException("Family member not found"));

        if (foundFamilyMember.getRole() == ROLE_OWNER && (role == ROLE_MEMBER || role == ROLE_MANAGER)) {
            throw new OwnerRoleDowngradeNotAllowedException("Owner cannot downgrade their own role.");
        }

        foundFamilyMember.setRole(role);

        return familyMemberRepository.save(foundFamilyMember);
    }

    public Family update(long familyId, UpdateFamilyDTO dto, User currentAuthorizedUser) {
        if (!isFamilyMember(familyId, currentAuthorizedUser.getId())) {
            throw new AccessDeniedException("Access denied exception");
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        FamilyMember currentFamilyMember = family.getMembers().stream()
                .filter(member -> member.getUser().equals(currentAuthorizedUser))
                .findFirst()
                .orElseThrow(() -> new FamilyMemberNotFoundException("Family member not found"));

        if (currentFamilyMember.getRole() != ROLE_OWNER && currentFamilyMember.getRole() != ROLE_MANAGER) {
            throw new AccessDeniedException("Only owner and manager can update family");
        }

        Optional.ofNullable(dto.getName())
                .ifPresent(family::setName);

        return familyRepository.save(family);
    }

    public boolean isFamilyMember(Family family, User user) {
        return familyRepository.existsByIdAndMembers_User_Id(family.getId(), user.getId());
    }

    public boolean isFamilyMember(long familyId, long userId) {
        return familyRepository.existsByIdAndMembers_User_Id(familyId, userId);
    }

    public List<Family> findAllFamilies() {
        return familyRepository.findAll();
    }

    public void deleteFamily(long familyId) {
        Family family = familyRepository.findById(familyId)
                        .orElseThrow(() -> new FamilyNotFoundException(familyId));
        familyRepository.delete(family);
    }
}
