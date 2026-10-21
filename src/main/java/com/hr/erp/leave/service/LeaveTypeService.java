package com.hr.erp.leave.service;

import com.hr.erp.leave.dto.request.CreateLeaveTypeRequestDto;
import com.hr.erp.leave.dto.request.UpdateLeaveTypeRequestDto;
import com.hr.erp.leave.dto.response.LeaveTypeResponseDto;

import java.util.List;

public interface LeaveTypeService {

    List<LeaveTypeResponseDto> getAllLeaveTypes();

    LeaveTypeResponseDto getLeaveTypeById(String leaveTypeId);

    LeaveTypeResponseDto createLeaveType(CreateLeaveTypeRequestDto request);

    LeaveTypeResponseDto updateLeaveType(String leaveTypeId, UpdateLeaveTypeRequestDto request);

}
