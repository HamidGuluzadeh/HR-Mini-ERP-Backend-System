package com.hr.erp.leave.controller;

import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.common.enums.SuccessStatus;
import com.hr.erp.leave.dto.request.CreateLeaveRequestDto;
import com.hr.erp.leave.dto.response.LeaveResponseDto;
import com.hr.erp.leave.model.LeaveStatus;
import com.hr.erp.leave.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
@Tag(name = "Leave Request Controller", description = "Endpoints for creating, reviewing, approving, rejecting, and canceling leave requests")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'LINE_MANAGER')")
    @Operation(summary = "Get leave requests by status", description = "Retrieves paginated leave requests filtered by state (PENDING, APPROVED, REJECTED)")
    public ResponseEntity<SuccessDto<Page<LeaveResponseDto>>> getLeaveRequestsByStatus(@RequestParam LeaveStatus status,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "20") int size) {
        Page<LeaveResponseDto> response = leaveRequestService.getLeaveRequestsByStatus(status, page, size);
        SuccessDto<Page<LeaveResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'LINE_MANAGER')")
    @Operation(summary = "Get leave requests by employee", description = "Retrieves paginated leave request history for a specific employee")
    public ResponseEntity<SuccessDto<Page<LeaveResponseDto>>> getLeaveRequestsByEmployee(@PathVariable String employeeId,
                                                                                         @RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "20") int size) {
        Page<LeaveResponseDto> response = leaveRequestService.getLeaveRequestsByEmployee(employeeId, page, size);
        SuccessDto<Page<LeaveResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{leaveRequestId}")
    @Operation(summary = "Get leave request by ID", description = "Retrieves detailed information of a specific leave request")
    public ResponseEntity<SuccessDto<LeaveResponseDto>> getLeaveRequestById(@PathVariable String leaveRequestId) {
        LeaveResponseDto response = leaveRequestService.getLeaveRequestById(leaveRequestId);
        SuccessDto<LeaveResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create leave request", description = "Submits a new time-off or leave application for an employee")
    public ResponseEntity<SuccessDto<LeaveResponseDto>> createLeaveRequest(@RequestBody @Valid CreateLeaveRequestDto request) {
        LeaveResponseDto response = leaveRequestService.createLeaveRequest(request);
        SuccessDto<LeaveResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{leaveRequestId}/approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'LINE_MANAGER')")
    @Operation(summary = "Approve leave request", description = "Approves a pending leave request and updates employee leave quota balance")
    public ResponseEntity<SuccessDto<LeaveResponseDto>> approveLeaveRequest(@PathVariable String leaveRequestId) {
        LeaveResponseDto response = leaveRequestService.approveLeaveRequest(leaveRequestId);
        SuccessDto<LeaveResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PatchMapping("/{leaveRequestId}/denial")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'LINE_MANAGER')")
    @Operation(summary = "Reject leave request", description = "Rejects a pending leave request with optional rejection comments")
    public ResponseEntity<SuccessDto<LeaveResponseDto>> rejectLeaveRequest(@PathVariable String leaveRequestId) {
        LeaveResponseDto response = leaveRequestService.rejectLeaveRequest(leaveRequestId);
        SuccessDto<LeaveResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PatchMapping("/{leaveRequestId}/cancel")
    @Operation(summary = "Cancel leave request", description = "Cancels a pending or scheduled leave request by the requester or manager")
    public ResponseEntity<SuccessDto<LeaveResponseDto>> cancelLeaveRequest(@PathVariable String leaveRequestId) {
        LeaveResponseDto response = leaveRequestService.cancelLeaveRequest(leaveRequestId);
        SuccessDto<LeaveResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }
}
