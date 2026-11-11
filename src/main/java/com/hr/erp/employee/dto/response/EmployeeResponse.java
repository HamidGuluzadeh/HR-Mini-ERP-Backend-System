package com.hr.erp.employee.dto.response;

import com.hr.erp.employee.model.EmployeeStatus;
import com.hr.erp.employee.model.SalaryType;
import lombok.Builder;

import java.math.BigDecimal;
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
                               String bankName,
                               String iban,
                               SalaryType salaryType,
                               BigDecimal baseSalary,
                               BigDecimal hourlyRate,
                               EmployeeStatus status,
                               Instant createdAt,
                               String createdBy,
                               Instant updatedAt,
                               String updatedBy) {

}
