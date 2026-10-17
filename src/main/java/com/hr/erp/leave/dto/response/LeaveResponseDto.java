package com.hr.erp.leave.dto.response;

import com.hr.erp.leave.model.LeaveStatus;

import java.time.Instant;
import java.time.LocalDate;

public record LeaveResponseDto(String id,
                               String employeeId,
                               String employeeFullName,
                               String leaveTypeId,
                               String leaveTypeName,
                               LocalDate startDate,
                               LocalDate endDate,
                               Integer totalDays,
                               LeaveStatus status,
                               String approvedById,
                               String approvedByFullName,
                               String reason,
                               Instant createdAt,
                               Instant updatedAt) {

}
