package com.hr.erp.auth.dto.response;

import com.hr.erp.auth.model.UserRole;

import java.time.Instant;

public record UserResponseDto(String id,
                              String employeeId,
                              String employeeCode,
                              String employeeFullName,
                              UserRole role,
                              Boolean isActive,
                              Instant createdAt,
                              Instant updatedAt) {

}
