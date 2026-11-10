package com.hr.erp.auth.controller;

import com.hr.erp.auth.dto.request.LoginRequestDto;
import com.hr.erp.auth.dto.request.RefreshRequestDto;
import com.hr.erp.auth.dto.response.AuthResponseDto;
import com.hr.erp.auth.service.AuthService;
import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.common.enums.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "Endpoints for user authentication, token renewal, and session termination")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user credentials and returns JWT access and refresh tokens")
    public ResponseEntity<SuccessDto<AuthResponseDto>> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        AuthResponseDto response = authService.login(loginRequestDto);
        SuccessDto<AuthResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Issues a new JWT access token using a valid refresh token")
    public ResponseEntity<SuccessDto<AuthResponseDto>> refresh(@RequestBody @Valid RefreshRequestDto refreshRequestDto) {
        AuthResponseDto response = authService.refresh(refreshRequestDto);
        SuccessDto<AuthResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Terminates the current user session and invalidates access context")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.noContent().build();
    }
}
