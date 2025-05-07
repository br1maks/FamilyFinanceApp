package com.maximilian.familyfinance.controller;

import com.maximilian.familyfinance.dto.ResponseWrapper;
import com.maximilian.familyfinance.dto.UserDTO;
import com.maximilian.familyfinance.dto.family.FamilyDTO;
import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.entity.family.Family;
import com.maximilian.familyfinance.mapper.UserMapper;
import com.maximilian.familyfinance.mapper.family.FamilyMapper;
import com.maximilian.familyfinance.repository.UserRepository;
import com.maximilian.familyfinance.service.FamilyService;
import com.maximilian.familyfinance.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final FamilyService familyService;
    private final FamilyMapper familyMapper;

//    ===================================== USERS =====================================

    @GetMapping("/users")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ResponseWrapper<List<UserDTO>>> getAllUsers() {
        List<User> allUsers = userService.findAll();

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "All users fetched successfully",
                allUsers.stream()
                        .map(userMapper::toDTO)
                        .toList()
        ));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/users/ban/{userId}")
    public ResponseEntity<ResponseWrapper<Void>> banUser(
            @Valid
            @Min(value = 1, message = "User id can't be less than 1")
            @PathVariable long userId
    ) {
        userService.ban(userId);

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "User banned successfully"
        ));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/users/unban/{userId}")
    public ResponseEntity<ResponseWrapper<Void>> unbanUser(
            @Valid
            @Min(value = 1, message = "User id can't be less than 1")
            @PathVariable long userId
    ) {
        userService.unban(userId);

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "User unbanned successfully"
        ));
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteUser(
            @Valid
            @Min(value = 1, message = "User id can't be less than 1")
            @PathVariable long userId
    ) {
        userService.delete(userId);

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "User deleted successfully"
        ));
    }

//    ===================================== FAMILIES=====================================

    @GetMapping("/families")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ResponseWrapper<List<FamilyDTO>>> findAllFamilies() {
        List<Family> allFamilies = familyService.findAllFamilies();

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "All families fetched successfully",
                allFamilies.stream()
                        .map(familyMapper::toFamilyDTO)
                        .toList()
        ));
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/families/{familyId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteFamily(
            @Valid
            @Min(value = 1, message = "User id can't be less than 1")
            @PathVariable long familyId
    ) {
        familyService.deleteFamily(familyId);

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Family deleted successfully"
        ));
    }
}
