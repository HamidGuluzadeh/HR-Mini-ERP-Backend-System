package com.hr.erp.auth.service.implement;

import com.hr.erp.auth.dto.request.LoginRequestDto;
import com.hr.erp.auth.dto.request.RefreshRequestDto;
import com.hr.erp.auth.dto.response.AuthResponseDto;
import com.hr.erp.auth.entity.RefreshToken;
import com.hr.erp.auth.entity.User;
import com.hr.erp.auth.model.UserStatus;
import com.hr.erp.auth.repository.RefreshTokenRepository;
import com.hr.erp.auth.repository.UserRepository;
import com.hr.erp.auth.service.AuthService;
import com.hr.erp.auth.service.JwtService;
import com.hr.erp.common.exception.BadRequestException;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.common.exception.UnauthorizedException;
import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    RefreshTokenRepository refreshTokenRepository;
    UserRepository userRepository;
    EmployeeRepository employeeRepository;
    JwtService jwtService;
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        Employee employee = employeeRepository.findByEmployeeCode(loginRequestDto.employeeCode())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        User user = userRepository.findByEmployee(employee)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Incorrect password!");
        }

        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new BadRequestException("Your account is not ACTIVE! Contact the support!");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDto refresh(RefreshRequestDto refreshRequestDto) {
        String hashedToken = jwtService.hashToken(refreshRequestDto.refreshToken());

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token!"));

        if (!Objects.isNull(refreshTokenEntity.getRevokedAt()) || refreshTokenEntity.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException("Bu token istifadəyə yararsızdır! Yenidən giriş edin");
        }

        User user = refreshTokenEntity.getUser();

        refreshTokenEntity.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshTokenEntity);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    @Transactional
    public void logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String employeeCode = auth.getCredentials().toString();

        List<RefreshToken> validTokens = refreshTokenRepository.findAllByUserIdAndRevokedAtIsNull(employeeCode);

        if (!validTokens.isEmpty()) {
            validTokens.forEach(refreshTokenEntity -> refreshTokenEntity.setRevokedAt(Instant.now()));

            refreshTokenRepository.saveAll(validTokens);
        }
    }
}
