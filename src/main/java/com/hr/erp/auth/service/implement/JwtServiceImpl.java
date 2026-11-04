package com.hr.erp.auth.service.implement;

import com.hr.erp.auth.entity.RefreshToken;
import com.hr.erp.auth.entity.User;
import com.hr.erp.auth.repository.RefreshTokenRepository;
import com.hr.erp.auth.service.JwtService;
import com.hr.erp.config.jwt.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    final RefreshTokenRepository refreshTokenRepository;
    final JwtProperties jwtProperties;
    Key accessTokenKey;

    @Value("${jwt.secret}")
    String secretKey;

    @PostConstruct
    void init() {
        if (Objects.isNull(secretKey) || secretKey.length() < 32) {
            throw new IllegalStateException("JWT secret key must be at least 256 bits!");
        }

        this.accessTokenKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.accessToken().expirationMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(user.getId())
                .claim("employeeCode", user.getEmployee().getEmployeeCode())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(accessTokenKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.refreshToken().expirationDays(), ChronoUnit.DAYS);

        String tokenId = UUID.randomUUID().toString();
        String tokenValue = generateSecureRandomToken(64);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenId(tokenId)
                .tokenHash(hashToken(tokenValue))
                .expiryDate(expiry)
                .revokedAt(null)
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenValue;
    }

    @Override
    public String hashToken(String tokenValue) {
        return DigestUtils.sha256Hex(tokenValue);
    }

    @Override
    public Key getSigningKey() {
        return this.accessTokenKey;
    }

    private String generateSecureRandomToken(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < length; i++) {
            int idx = secureRandom.nextInt(chars.length());
            stringBuilder.append(chars.charAt(idx));
        }

        return stringBuilder.toString();
    }
}
