package com.hr.erp.payroll.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdatePayrollRequestDto(@NotNull(message = "Base salary is required!")
                                      @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than zero!")
                                      BigDecimal baseSalary,
                                      @DecimalMin(value = "0.0", message = "Bonus cannot be negative!")
                                      BigDecimal bonus,
                                      String bonusReason,
                                      @DecimalMin(value = "0.0", message = "Deductions cannot be negative!")
                                      BigDecimal deductions,
                                      String deductionReason) {

}
