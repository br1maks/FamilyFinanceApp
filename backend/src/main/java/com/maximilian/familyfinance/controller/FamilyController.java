package com.maximilian.familyfinance.controller;

import com.maximilian.familyfinance.dto.ResponseWrapper;
import com.maximilian.familyfinance.dto.family.*;
import com.maximilian.familyfinance.entity.family.Family;
import com.maximilian.familyfinance.entity.family.FamilyMember;
import com.maximilian.familyfinance.mapper.family.FamilyMapper;
import com.maximilian.familyfinance.mapper.family.FamilyMemberMapper;
import com.maximilian.familyfinance.mapper.family.GoalMapper;
import com.maximilian.familyfinance.service.FamilyService;
import com.maximilian.familyfinance.service.GoalService;
import com.maximilian.familyfinance.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/families")
public class FamilyController {

    private final FamilyService familyService;
    private final UserService userService;
    private final FamilyMapper familyMapper;
    private final FamilyMemberMapper familyMemberMapper;
    private final GoalService goalService;
    private final GoalMapper goalMapper;

    @PostMapping
    public ResponseEntity<ResponseWrapper<FamilyDTO>> createFamily(@Valid @RequestBody CreateFamilyDTO dto) {
        Family createdFamily = familyService.create(dto, userService.getCurrentAuthorizedUser());

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/me/families/%s".formatted(createdFamily.getId())
                ))
                .body(ResponseWrapper.success(
                        HttpStatus.CREATED,
                        "Family created successfully",
                        familyMapper.toFamilyDTO(createdFamily)
                ));
    }

    @DeleteMapping("/{familyId}")
    public ResponseEntity<ResponseWrapper<?>> deleteFamilyById(
            @Min(value = 1, message = "Id семьи должно быть больше, либо равно 1")
            @PathVariable long familyId
    ) {
        familyService.deleteFamilyIfOwned(familyId, userService.getCurrentAuthorizedUser());

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.NO_CONTENT,
                "Family deleted successfully"
        ));
    }

    @GetMapping("/{familyId}/invite-code")
    public ResponseEntity<ResponseWrapper<String>> getInviteCodeByFamilyId(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо равно 1")
            @PathVariable long familyId
    ) {
        String inviteCode = familyService.getFamilyInviteCodeIfOwner(familyId, userService.getCurrentAuthorizedUser());
        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Invite code fetched successfully",
                inviteCode
        ));
    }

    @PostMapping("/join")
    public ResponseEntity<ResponseWrapper<FamilyDTO>> joinFamilyByInviteCode(
            @Valid @RequestBody JoinByInviteCodeDTO dto
    ) {
        Family family = familyService.joinFamilyByInviteCode(dto.getInviteCode(), userService.getCurrentAuthorizedUser());
        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Successfully joined to family",
                familyMapper.toFamilyDTO(family)
        ));
    }

    @PostMapping("/{familyId}/kick")
    public ResponseEntity<ResponseWrapper<FamilyDTO>> kickMember(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо равно 1")
            @PathVariable long familyId,

            @Valid
            @RequestBody KickMemberDTO dto
    ) {
        Family family = familyService.kickMemberIfOwnerOrManager(
                familyId,
                dto.getUserId(),
                userService.getCurrentAuthorizedUser()
        );

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Member kicked successfully",
                familyMapper.toFamilyDTO(family)
        ));
    }

    @GetMapping("/{familyId}")
    public ResponseEntity<ResponseWrapper<FamilyDTO>> getById(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо ровняться 1")
            @PathVariable long familyId
    ) {
        Family foundFamily = familyService.getByIdIfMember(familyId, userService.getCurrentAuthorizedUser());

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Family fetched successfully",
                familyMapper.toFamilyDTO(foundFamily)
        ));
    }

    @PostMapping("/{familyId}/leave")
    public ResponseEntity<ResponseWrapper<Void>> leave(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо ровняться 1")
            @PathVariable long familyId
    ) {
        familyService.leave(familyId, userService.getCurrentAuthorizedUser());

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "You have successfully left the family"
        ));
    }

    @PatchMapping("/{familyId}/members/{memberId}/role")
    public ResponseEntity<ResponseWrapper<FamilyMemberDTO>> SetFamilyMemberRole(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо ровняться 1")
            @PathVariable long familyId,

            @Valid @Min(value = 1, message = "Id участника должно быть больше, либо ровняться 1")
            @PathVariable long memberId,

            @Valid
            @RequestBody SetFamilyMemberRoleDTO dto
    ) {
        FamilyMember familyMember = familyService.SetFamilyMemberRole(familyId, memberId, dto.getRole(), userService.getCurrentAuthorizedUser());

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Role set successfully",
                familyMemberMapper.toDTO(familyMember)
        ));
    }

    @PatchMapping("/{familyId}")
    public ResponseEntity<ResponseWrapper<FamilyDTO>> updateFamily(
            @Valid @Min(value = 1, message = "Id семьи должно быть больше, либо ровняться 1")
            @PathVariable long familyId,

            @Valid
            @RequestBody UpdateFamilyDTO dto
    ) {
        Family updatedFamily = familyService.update(familyId, dto, userService.getCurrentAuthorizedUser());

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Family updated successfully",
                familyMapper.toFamilyDTO(updatedFamily)
        ));
    }
}
