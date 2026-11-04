package com.hr.erp.auth.dto.request;

import com.hr.erp.auth.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(@NotBlank(message = "Employee ID is required!")
                                   String employeeId,
                                   @NotBlank(message = "Password is required!")
                                   @Size(min = 6, message = "Password must be at least 6 characters long!")
                                   String password,
                                   @NotNull(message = "User role is required!")
                                   UserRole role) {

}
