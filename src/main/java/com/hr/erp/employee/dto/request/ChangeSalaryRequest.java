package com.hr.erp.employee.dto.request;

import com.hr.erp.employee.model.SalaryType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChangeSalaryRequest(@NotNull(message = "Salary type is required!")
                                  SalaryType salaryType,
                                  BigDecimal baseSalary,
                                  BigDecimal hourlyRate,
                                  @NotNull(message = "Effective from date is required!")
                                  LocalDate effectiveFrom) {

}
