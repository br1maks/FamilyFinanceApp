package com.maximilian.familyfinance.controller;

import com.maximilian.familyfinance.dto.family.FamilyDTO;
import com.maximilian.familyfinance.dto.ResponseWrapper;
import com.maximilian.familyfinance.dto.UserDTO;
import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.mapper.family.FamilyMapper;
import com.maximilian.familyfinance.mapper.UserMapper;
import com.maximilian.familyfinance.service.FamilyService;
import com.maximilian.familyfinance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final FamilyService familyService;
    private final FamilyMapper familyMapper;

    @GetMapping
    public ResponseEntity<ResponseWrapper<UserDTO>> getMeInfo() {
        User currentUser = userService.getCurrentAuthorizedUser();

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "User fetched successfully",
                userMapper.toDTO(currentUser)
        ));
    }

    @GetMapping("/families")
    public ResponseEntity<ResponseWrapper<List<FamilyDTO>>> getFamilies() {
        User currentUser = userService.getCurrentAuthorizedUser();
        List<FamilyDTO> families = familyService.findFamiliesByMembersId(currentUser.getId()).stream()
                .map(familyMapper::toFamilyDTO)
                .toList();

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Family memberships fetched successfully",
                families
        ));
    }
}
