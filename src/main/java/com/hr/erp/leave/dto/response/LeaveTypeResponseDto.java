package com.hr.erp.leave.dto.response;

import java.time.Instant;

public record LeaveTypeResponseDto(String id,
                                   String name,
                                   Integer maxDaysPerYear,
                                   Boolean isPaid,
                                   Instant createdAt,
                                   Instant updatedAt) {

}
