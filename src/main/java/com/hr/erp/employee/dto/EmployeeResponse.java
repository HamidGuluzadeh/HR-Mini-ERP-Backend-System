package com.hr.erp.employee.dto;

import com.hr.erp.common.enums.EmployeeStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder
public record EmployeeResponse(String id,
                               String firstName,
                               String lastName,
                               String email,
                               String phoneNumber,
                               LocalDate hireDate,
                               LocalDate terminationDate,
                               String positionId,
                               String positionTitle,
                               String departmentName,
                               String managerId,
                               String managerFullName,
                               EmployeeStatus status,
                               Instant createdAt,
                               Instant updatedAt) {
}
