package com.maximilian.familyfinance.controller;

import com.maximilian.familyfinance.dto.auth.LoginDTO;
import com.maximilian.familyfinance.dto.auth.RefreshAccessTokenDTO;
import com.maximilian.familyfinance.dto.auth.RegisterDTO;
import com.maximilian.familyfinance.dto.ResponseWrapper;
import com.maximilian.familyfinance.dto.UserDTO;
import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.mapper.UserMapper;
import com.maximilian.familyfinance.service.AuthenticationService;
import com.maximilian.familyfinance.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authService;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<UserDTO>> login(@Valid @RequestBody LoginDTO dto) {
        User loginedUser = authService.login(dto);
        String accessToken = jwtService.buildAccessToken(loginedUser.getId());
        String refreshToken = jwtService.buildRefreshToken(loginedUser.getId());

        return ResponseEntity.ok(ResponseWrapper.success(
                        HttpStatus.OK,
                        "Login successful",
                        userMapper.toDTO(loginedUser)
                )
                .addField("accessToken", accessToken)
                .addField("refreshToken", refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<UserDTO>> register(@Valid @RequestBody RegisterDTO dto) {
        User createdUser = authService.register(dto);
        return ResponseEntity
                .created(URI.create(
                        "/api/v1/users/%d".formatted(createdUser.getId())
                ))
                .body(ResponseWrapper.success(
                        HttpStatus.CREATED,
                        "User created successfully",
                        userMapper.toDTO(createdUser)
                ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseWrapper<String>> refreshAccessToken(
            @Valid
            @RequestBody RefreshAccessTokenDTO dto
    ) {
        String accessToken = jwtService.refreshAccessToken(dto.getRefreshToken());

        return ResponseEntity.ok(ResponseWrapper.success(
                HttpStatus.OK,
                "Access token refreshed successfully",
                accessToken
        ));
    }
}
