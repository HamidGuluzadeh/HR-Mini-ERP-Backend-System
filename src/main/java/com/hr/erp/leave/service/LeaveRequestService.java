package com.hr.erp.leave.service;

import com.hr.erp.leave.dto.request.CreateLeaveRequestDto;
import com.hr.erp.leave.dto.response.LeaveResponseDto;
import com.hr.erp.leave.model.LeaveStatus;
import org.springframework.data.domain.Page;

public interface LeaveRequestService {

    Page<LeaveResponseDto> getLeaveRequestsByStatus(LeaveStatus status, int page, int size);

    Page<LeaveResponseDto> getLeaveRequestsByEmployee(String employeeId, int page, int size);

    LeaveResponseDto getLeaveResponseById(String leaveRequestId);

    LeaveResponseDto createLeaveRequest(CreateLeaveRequestDto request);

    LeaveResponseDto approveLeaveRequest(String leaveRequestId);

    LeaveResponseDto rejectLeaveRequest(String leaveRequestId);

    LeaveResponseDto cancelLeaveRequest(String leaveRequestId);

}
