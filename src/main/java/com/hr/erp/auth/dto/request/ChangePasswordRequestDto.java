package com.hr.erp.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(@NotBlank(message = "Old password is required!")
                                       String oldPassword,
                                       @NotBlank(message = "New password is required!")
                                       @Size(min = 6, message = "New password must be at least 6 characters long!")
                                       String newPassword) {

}
