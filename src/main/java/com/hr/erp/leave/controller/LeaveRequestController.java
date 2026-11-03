package com.hr.erp.leave.controller;

import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.common.enums.SuccessStatus;
import com.hr.erp.leave.dto.request.CreateLeaveRequestDto;
import com.hr.erp.leave.dto.response.LeaveResponseDto;
import com.hr.erp.leave.model.LeaveStatus;
import com.hr.erp.leave.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;

    @GetMapping("/status")
    public ResponseEntity<SuccessDto<Page<LeaveResponseDto>>> getLeaveRequestsByStatus(@RequestParam LeaveStatus status,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "20") int size) {
        Page<LeaveResponseDto> response = leaveRequestService.getLeaveRequestsByStatus(status, page, size);
        SuccessDto<Page<LeaveResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<SuccessDto<Page<LeaveResponseDto>>> getLeaveRequestsByEmployee(@PathVariable String employeeId,
                                                                                         @RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "20") int size) {
        Page<LeaveResponseDto> response = leaveRequestService.getLeaveRequestsByEmployee(employeeId, page, size);
        SuccessDto<Page<LeaveResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{leaveRequestId}")
    public ResponseEntity<SuccessDto<LeaveResponseDto>> getLeaveRequestById(@PathVariable String leaveRequestId) {
        LeaveResponseDto response = leaveRequestService.getLeaveRequestById(leaveRequestId);
        SuccessDto<LeaveResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SuccessDto<LeaveResponseDto>> createLeaveRequest(@RequestBody @Valid CreateLeaveRequestDto request) {
        LeaveResponseDto response = leaveRequestService.createLeaveRequest(request);
        SuccessDto<LeaveResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PatchMapping("/approval/{leaveRequestId}")
    public ResponseEntity<SuccessDto<LeaveResponseDto>> approveLeaveRequest(@PathVariable String leaveRequestId) {
        LeaveResponseDto response = leaveRequestService.approveLeaveRequest(leaveRequestId);
        SuccessDto<LeaveResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PatchMapping("/denial/{leaveRequestId}")
    public ResponseEntity<SuccessDto<LeaveResponseDto>> rejectLeaveRequest(@PathVariable String leaveRequestId) {
        LeaveResponseDto response = leaveRequestService.rejectLeaveRequest(leaveRequestId);
        SuccessDto<LeaveResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PatchMapping("/cancel/{leaveRequestId}")
    public ResponseEntity<SuccessDto<LeaveResponseDto>> cancelLeaveRequest(@PathVariable String leaveRequestId) {
        LeaveResponseDto response = leaveRequestService.cancelLeaveRequest(leaveRequestId);
        SuccessDto<LeaveResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }
}
