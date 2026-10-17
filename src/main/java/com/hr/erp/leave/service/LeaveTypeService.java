package com.hr.erp.leave.service;

import com.hr.erp.leave.dto.request.LeaveTypeRequestDto;
import com.hr.erp.leave.dto.response.LeaveTypeResponseDto;

import java.util.List;

public interface LeaveTypeService {

    List<LeaveTypeResponseDto> getAllLeaveTypes();

    LeaveTypeResponseDto getLeaveTypeById(String leaveTypeId);

    LeaveTypeResponseDto createLeaveType(LeaveTypeRequestDto request);

    LeaveTypeResponseDto updateLeaveType(String leaveTypeId, LeaveTypeRequestDto request);

    void deleteLeaveType(String leaveTypeId);

}
