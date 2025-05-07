package com.maximilian.familyfinance.config;

import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.maximilian.familyfinance.enums.user.UserRole.ROLE_OWNER;

@Slf4j
@Configuration
public class DataConfig {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (!userRepository.existsByUsernameIgnoreCase("admin")) {
                User adminUser = User.builder()
                        .role(ROLE_OWNER)
                        .username("admin")
                        .firstName("Admin")
                        .lastName("Admin")
                        .password(passwordEncoder.encode("Admin1234"))
                        .build();

                userRepository.save(adminUser);
                log.debug("Admin user created");
            } else {
                log.debug("Admin user already created");
            }
        };
    }
}
