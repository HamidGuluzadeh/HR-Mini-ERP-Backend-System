package com.hr.erp.leave.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLeaveTypeRequestDto(@NotBlank(message = "Leave type name is required!")
                                        @Size(max = 50, message = "Leave type name must not exceed 50 characters!")
                                        String name,
                                        @NotNull(message = "Max days per year is required!")
                                        @Min(value = 1, message = "Max days per year must be at least 1!")
                                        Integer maxDaysPerYear,
                                        @NotNull(message = "isPaid field is required!")
                                        Boolean isPaid) {

}
