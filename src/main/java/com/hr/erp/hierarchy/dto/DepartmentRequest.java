package com.hr.erp.hierarchy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record DepartmentRequest(@NotBlank(message = "Department name is required!")
                                @Size(max = 100, message = "Name cannot exceed 100 characters!")
                                String name,
                                @NotBlank(message = "Department code is required!")
                                @Size(max = 20, message = "Code cannot exceed 20 characters!")
                                String code,
                                String parentId) {

}
