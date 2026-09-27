package com.hr.erp.hierarchy.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DepartmentResponse(String id,
                                 String name,
                                 String code,
                                 String parentId,
                                 Instant createdAt,
                                 Instant updatedAt) {

}
