package com.maximilian.familyfinance.service;

import com.maximilian.familyfinance.dto.auth.LoginDTO;
import com.maximilian.familyfinance.dto.auth.RegisterDTO;
import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.exception.user.UserAlreadyExistsException;
import com.maximilian.familyfinance.exception.user.UserNotFoundException;
import com.maximilian.familyfinance.mapper.UserMapper;
import com.maximilian.familyfinance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final UserMapper userMapper;

    public User register(RegisterDTO dto) {
        return register(userMapper.toUser(dto));
    }

    private User register(User user) {
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new UserAlreadyExistsException("User with username '%s' already exists".formatted(user.getUsername()));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User login(LoginDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Invalid username or password"));

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()
                )
        );

        return user;
    }
}
