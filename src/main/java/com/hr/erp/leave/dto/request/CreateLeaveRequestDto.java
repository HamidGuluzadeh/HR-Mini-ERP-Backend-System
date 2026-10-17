package com.hr.erp.leave.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateLeaveRequestDto(@NotBlank(message = "Employee ID is required!")
                                    String employeeId,
                                    @NotBlank(message = "Leave type ID is required!")
                                    String leaveTypeId,
                                    @NotNull(message = "Start date is required!")
                                    @FutureOrPresent(message = "Start date cannot be in the past!")
                                    LocalDate startDate,
                                    @NotNull(message = "End date is required!")
                                    @FutureOrPresent(message = "End date cannot be in the past!")
                                    LocalDate endDate,
                                    @Size(max = 500, message = "Reason must not exceed 500 characters!")
                                    String reason) {

}
