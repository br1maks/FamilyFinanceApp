package com.maximilian.familyfinance.service;

import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.exception.auth.JwtAuthenticationException;
import com.maximilian.familyfinance.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    @Getter
    private final long JWT_ACCESS_EXPIRATION_MILLS;

    @Getter
    private final long JWT_REFRESH_EXPIRATION_MILLS;

    private final String JWT_SECRET_KEY;
    private final UserRepository userRepository;

    public JwtService(
            @Value("${jwt.access-expiration}")
            long JWT_ACCESS_EXPIRATION_MILLS,

            @Value("${jwt.refresh-expiration}")
            long JWT_REFRESH_EXPIRATION_MILLS,

            @Value("${jwt.secret-key}")
            String JWT_SECRET_KEY,
            UserRepository userRepository) {
        this.JWT_ACCESS_EXPIRATION_MILLS = JWT_ACCESS_EXPIRATION_MILLS;
        this.JWT_REFRESH_EXPIRATION_MILLS = JWT_REFRESH_EXPIRATION_MILLS;
        this.JWT_SECRET_KEY = JWT_SECRET_KEY;
        this.userRepository = userRepository;
    }

    public String refreshAccessToken(String refreshToken) {
        if (!isSupportedJwt(refreshToken)) {
            throw new JwtAuthenticationException("Invalid refresh token");
        }

        long userId = extractUserId(refreshToken);
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty() || !isTokenValid(refreshToken, userOptional.get())) {
            throw new JwtAuthenticationException("Invalid refresh token");
        }

        return buildAccessToken(userId);
    }

    public String buildAccessToken(long userId) {
        return buildToken(
                new HashMap<>(),
                userId,
                JWT_ACCESS_EXPIRATION_MILLS
        );
    }

    public String buildRefreshToken(long userId) {
        return buildToken(
                new HashMap<>(),
                userId,
                JWT_REFRESH_EXPIRATION_MILLS
        );
    }

    public long extractUserId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    public boolean isTokenValid(String token, User user) {
        final long extractedUserId = extractUserId(token);
        return !isTokenExpired(token) && (user.getId() == extractedUserId);
    }

    public boolean isSupportedJwt(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return e.getClaims() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            long userId,
            long jwtExpirationTime
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(String.valueOf(userId))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(getSignInKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationTime(token).before(new Date());
    }

    private Date extractExpirationTime(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        if (!isSupportedJwt(token)) {
            throw new UnsupportedJwtException("Unsupported Jwt!");
        }

        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes());
    }
}
