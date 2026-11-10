package com.hr.erp.auth.controller;

import com.hr.erp.auth.dto.request.ChangePasswordRequestDto;
import com.hr.erp.auth.dto.request.CreateUserRequestDto;
import com.hr.erp.auth.dto.response.UserResponseDto;
import com.hr.erp.auth.model.UserStatus;
import com.hr.erp.auth.service.UserService;
import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.common.enums.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for system user account administration, credentials, and account statuses")
public class UserController {
    private final UserService userService;

    @GetMapping("/department/{departmentId}/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    @Operation(summary = "Get users by department ID", description = "Retrieves a paginated list of system users assigned to a specific department ID")
    public ResponseEntity<SuccessDto<Page<UserResponseDto>>> getUsersByDepartmentId(@PathVariable String departmentId,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "20") int size) {
        Page<UserResponseDto> response = userService.getUsersByDepartmentId(departmentId, page, size);
        SuccessDto<Page<UserResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/department/code/{departmentCode}/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    @Operation(summary = "Get users by department code", description = "Retrieves a paginated list of system users assigned to a specific department code")
    public ResponseEntity<SuccessDto<Page<UserResponseDto>>> getUsersByDepartmentCode(@PathVariable String departmentCode,
                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "20") int size) {
        Page<UserResponseDto> response = userService.getUsersByDepartmentCode(departmentCode, page, size);
        SuccessDto<Page<UserResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    @Operation(summary = "Get user by ID", description = "Retrieves detailed profile information for a specific user ID")
    public ResponseEntity<SuccessDto<UserResponseDto>> getUserById(@PathVariable String userId) {
        UserResponseDto response = userService.getUserById(userId);
        SuccessDto<UserResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user account", description = "Creates a new system user account with assigned role and credentials")
    public ResponseEntity<SuccessDto<UserResponseDto>> createUser(@RequestBody @Valid CreateUserRequestDto request) {
        UserResponseDto response = userService.createUser(request);
        SuccessDto<UserResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PatchMapping("/password")
    @Operation(summary = "Change password", description = "Updates the security password for the currently authenticated user")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequestDto request) {
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change user status", description = "Updates operational status (ACTIVE, INACTIVE, BLOCKED) of a user account")
    public ResponseEntity<Void> changeStatus(@PathVariable String userId, @RequestParam UserStatus status) {
        userService.changeStatus(userId, status);
        return ResponseEntity.ok().build();
    }
}
