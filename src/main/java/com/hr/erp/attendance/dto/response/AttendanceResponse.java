package com.hr.erp.attendance.dto.response;

import com.hr.erp.common.enums.AttendanceStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder
public record AttendanceResponse(String id,
                                 String employeeId,
                                 String firstName,
                                 String lastName,
                                 LocalDate workDate,
                                 Instant checkIn,
                                 Instant checkOut,
                                 AttendanceStatus status,
                                 Instant createdAt) {

}
