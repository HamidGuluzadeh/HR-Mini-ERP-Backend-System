package com.hr.erp.attendance.dto.request;

import com.hr.erp.common.enums.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder
public record AttendanceRequest(@NotBlank(message = "Employee ID is required!")
                                String employeeId,
                                @NotNull(message = "Work date is required!")
                                LocalDate workDate,
                                Instant checkIn,
                                Instant checkOut,
                                @NotNull(message = "Attendance status is required!")
                                AttendanceStatus status) {

}
