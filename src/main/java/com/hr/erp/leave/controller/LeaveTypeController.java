package com.hr.erp.leave.controller;

import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.common.enums.SuccessStatus;
import com.hr.erp.leave.dto.request.CreateLeaveTypeRequestDto;
import com.hr.erp.leave.dto.request.UpdateLeaveTypeRequestDto;
import com.hr.erp.leave.dto.response.LeaveTypeResponseDto;
import com.hr.erp.leave.service.LeaveTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-types")
@RequiredArgsConstructor
public class LeaveTypeController {
    private final LeaveTypeService leaveTypeService;

    @GetMapping("/all")
    public ResponseEntity<SuccessDto<List<LeaveTypeResponseDto>>> getAllLeaveTypes() {
        List<LeaveTypeResponseDto> response = leaveTypeService.getAllLeaveTypes();
        SuccessDto<List<LeaveTypeResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{leaveTypeId}")
    public ResponseEntity<SuccessDto<LeaveTypeResponseDto>> getLeaveTypeById(@PathVariable String leaveTypeId) {
        LeaveTypeResponseDto response = leaveTypeService.getLeaveTypeById(leaveTypeId);
        SuccessDto<LeaveTypeResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SuccessDto<LeaveTypeResponseDto>> createLeaveType(@RequestBody @Valid CreateLeaveTypeRequestDto request) {
        LeaveTypeResponseDto response = leaveTypeService.createLeaveType(request);
        SuccessDto<LeaveTypeResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{leaveTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SuccessDto<LeaveTypeResponseDto>> updateLeaveType(@PathVariable String leaveTypeId,
                                                                            @RequestBody @Valid UpdateLeaveTypeRequestDto request) {
        LeaveTypeResponseDto response = leaveTypeService.updateLeaveType(leaveTypeId, request);
        SuccessDto<LeaveTypeResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }
}
