package com.hr.erp.leave.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateLeaveTypeRequestDto(@Size(max = 50, message = "Leave type name must not exceed 50 characters!")
                                        String name,
                                        @Min(value = 1, message = "Max days per year must be at least 1!")
                                        Integer maxDaysPerYear,
                                        Boolean isPaid) {

}
