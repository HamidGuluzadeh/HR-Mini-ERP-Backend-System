package com.hr.erp.hierarchy.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PositionResponse(String id,
                               String title,
                               String departmentId,
                               BigDecimal minSalary,
                               BigDecimal maxSalary,
                               Instant createdAt,
                               Instant updatedAt) {

}
