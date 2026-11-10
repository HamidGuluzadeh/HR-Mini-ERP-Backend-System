package com.hr.erp.payroll.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePayrollRequestDto(@NotBlank(message = "Employee ID is required!")
                                      String employeeId,
                                      @NotNull(message = "Period start date is required!")
                                      LocalDate periodStart,
                                      @NotNull(message = "Period end date is required!")
                                      LocalDate periodEnd,
                                      @NotNull(message = "Base salary is required!")
                                      @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than zero!")
                                      BigDecimal baseSalary,
                                      @DecimalMin(value = "0.0", message = "Bonus cannot be negative!")
                                      BigDecimal bonus,
                                      String bonusReason,
                                      @DecimalMin(value = "0.0", message = "Deductions cannot be negative!")
                                      BigDecimal deductions,
                                      String deductionReason) {

}
