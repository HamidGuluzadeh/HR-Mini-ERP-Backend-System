package com.hr.erp.employee.dto;

import com.hr.erp.common.enums.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EmployeeRequest(@NotBlank(message = "First name is required!")
                              @Size(max = 50, message = "First name cannot exceed 50 characters!")
                              String firstName,
                              @NotBlank(message = "Last name is required!")
                              @Size(max = 50, message = "Last name cannot exceed 50 characters!")
                              String lastName,
                              @Email(message = "Invalid email format!")
                              @Size(max = 100, message = "Email cannot exceed 100 characters!")
                              String email,
                              @NotBlank(message = "Phone number is required!")
                              @Size(max = 20, message = "Phone number cannot exceed 20 characters!")
                              String phoneNumber,
                              @NotNull(message = "Hire date is required!")
                              LocalDate hireDate,
                              LocalDate terminationDate,
                              @NotBlank(message = "Position ID is required!")
                              String positionId,
                              String managerId,
                              EmployeeStatus status) {
}
