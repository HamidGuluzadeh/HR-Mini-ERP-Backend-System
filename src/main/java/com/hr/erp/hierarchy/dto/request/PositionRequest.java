package com.hr.erp.hierarchy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PositionRequest(@NotBlank(message = "Position title is required!")
                              @Size(max = 100, message = "Title cannot exceed more than 100 characters!")
                              String title,
                              @NotBlank(message = "Department ID is required!")
                              String departmentId,
                              @NotNull(message = "Minimum salary is required!")
                              @Positive(message = "Minimum salary must be positive!")
                              BigDecimal minSalary,
                              @NotNull(message = "Maximum salary is required!")
                              @Positive(message = "Maximum salary must be positive!")
                              BigDecimal maxSalary) {

}
