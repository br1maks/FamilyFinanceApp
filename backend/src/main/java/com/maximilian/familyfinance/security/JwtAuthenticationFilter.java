package com.maximilian.familyfinance.security;

import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.exception.auth.JwtAuthenticationException;
import com.maximilian.familyfinance.repository.UserRepository;
import com.maximilian.familyfinance.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthenticationEntryPoint authEntryPoint;
    private final UserRepository userRepository;

    public final String BEARER_PREFIX = "Bearer ";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestUri = request.getRequestURI();
        return requestUri.startsWith("/api/v1/auth");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.debug("Authorization token: {}", token);

        if (token == null) {
            log.debug("Auth token not found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            authEntryPoint.commence(request, response, new JwtAuthenticationException("Auth token not found"));
            return;
        }

        if (!token.startsWith(BEARER_PREFIX) || !jwtService.isSupportedJwt(token.substring(BEARER_PREFIX.length()))) {
            log.debug("Invalid authentication token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            authEntryPoint.commence(request, response, new JwtAuthenticationException("Invalid authentication token"));
            return;
        }

        token = token.substring(BEARER_PREFIX.length());
        long userId;

        try {
            userId = jwtService.extractUserId(token);
        } catch (NumberFormatException e) {
            log.debug("Invalid authentication token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            authEntryPoint.commence(request, response, new JwtAuthenticationException("Invalid authentication token"));
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            log.debug("User already authenticated, stopping jwt filter...");
            filterChain.doFilter(request, response);
            return;
        }

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            log.debug("User with id '{}' not found", userId);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            authEntryPoint.commence(request, response, new JwtAuthenticationException("User not found"));
            return;
        }

        User user = userOptional.get();

        if (!user.getIsAccountNonLocked()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            authEntryPoint.commence(request, response, new JwtAuthenticationException("Account is locked"));
            return;
        }

        if (!jwtService.isTokenValid(token, user)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            authEntryPoint.commence(request, response, new JwtAuthenticationException("Invalid authentication token"));
            return;
        }

        var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        log.info("Successfully authenticated user with id '{}'", userId);

        filterChain.doFilter(request, response);
    }
}
