package com.hr.erp.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(@NotBlank(message = "Employee code is required!")
                              String employeeCode,
                              @NotBlank(message = "Password is required!")
                              @Size(min = 6, message = "Password must be at least 6 characters long!")
                              String password) {

}
