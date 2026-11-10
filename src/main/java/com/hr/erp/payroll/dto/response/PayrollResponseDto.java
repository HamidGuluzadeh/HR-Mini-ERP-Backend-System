package com.hr.erp.payroll.dto.response;

import com.hr.erp.payroll.model.PayrollStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PayrollResponseDto(String id,
                                 String employeeId,
                                 String employeeFullName,
                                 String employeeCode,
                                 String positionName,
                                 String departmentName,
                                 LocalDate periodStart,
                                 LocalDate periodEnd,
                                 BigDecimal baseSalary,
                                 BigDecimal bonus,
                                 String bonusReason,
                                 BigDecimal deductions,
                                 String deductionReason,
                                 BigDecimal netSalary,
                                 PayrollStatus status,
                                 Instant createdAt,
                                 String createdBy,
                                 Instant updatedAt,
                                 String updatedBy) {

}
