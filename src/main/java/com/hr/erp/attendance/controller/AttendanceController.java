package com.hr.erp.attendance.controller;

import com.hr.erp.attendance.dto.request.AttendanceRequest;
import com.hr.erp.attendance.dto.response.AttendanceResponse;
import com.hr.erp.attendance.service.AttendanceService;
import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.common.enums.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Controller", description = "Endpoints for managing employee attendance, check-in/check-out, and attendance adjustments")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/employee/{employeeId}/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'LINE_MANAGER')")
    @Operation(summary = "Get attendances by employee", description = "Retrieves paginated attendance history for a specific employee")
    public ResponseEntity<SuccessDto<Page<AttendanceResponse>>> getAttendancesByEmployee(@PathVariable String employeeId,
                                                                                         @RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "20") int size) {
        Page<AttendanceResponse> response = attendanceService.getAttendancesByEmployee(employeeId, page, size);
        SuccessDto<Page<AttendanceResponse>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/date")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'LINE_MANAGER')")
    @Operation(summary = "Get attendances by date", description = "Retrieves paginated attendance records for a specific work date")
    public ResponseEntity<SuccessDto<Page<AttendanceResponse>>> getAttendancesByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
                                                                                     @RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "20") int size) {
        Page<AttendanceResponse> response = attendanceService.getAttendancesByDate(workDate, page, size);
        SuccessDto<Page<AttendanceResponse>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'LINE_MANAGER')")
    @Operation(summary = "Get attendances by date range", description = "Retrieves paginated attendance records within a specified date interval")
    public ResponseEntity<SuccessDto<Page<AttendanceResponse>>> getAttendancesByDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                                          @RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "20") int size) {
        Page<AttendanceResponse> response = attendanceService.getAttendancesByDateRange(startDate, endDate, page, size);
        SuccessDto<Page<AttendanceResponse>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{attendanceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'LINE_MANAGER')")
    @Operation(summary = "Get attendance by ID", description = "Retrieves detailed information of a specific attendance record")
    public ResponseEntity<SuccessDto<AttendanceResponse>> getAttendanceById(@PathVariable String attendanceId) {
        AttendanceResponse response = attendanceService.getAttendanceById(attendanceId);
        SuccessDto<AttendanceResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/check-in/{employeeId}")
    @Operation(summary = "Check-in employee", description = "Records the work arrival time for an employee")
    public ResponseEntity<SuccessDto<AttendanceResponse>> checkIn(@PathVariable String employeeId) {
        AttendanceResponse response = attendanceService.checkIn(employeeId);
        SuccessDto<AttendanceResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/check-out/{employeeId}")
    @Operation(summary = "Check-out employee", description = "Records the work departure time for an employee")
    public ResponseEntity<SuccessDto<AttendanceResponse>> checkOut(@PathVariable String employeeId) {
        AttendanceResponse response = attendanceService.checkOut(employeeId);
        SuccessDto<AttendanceResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'LINE_MANAGER')")
    @Operation(summary = "Create manual attendance", description = "Manually creates an attendance entry for an employee")
    public ResponseEntity<SuccessDto<AttendanceResponse>> createAttendance(@RequestBody @Valid AttendanceRequest request) {
        AttendanceResponse response = attendanceService.createAttendance(request);
        SuccessDto<AttendanceResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{attendanceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'LINE_MANAGER')")
    @Operation(summary = "Update attendance record", description = "Updates existing attendance details such as check-in or check-out timestamps")
    public ResponseEntity<SuccessDto<AttendanceResponse>> updateAttendance(@PathVariable String attendanceId,
                                                                           @RequestBody @Valid  AttendanceRequest request) {
        AttendanceResponse response = attendanceService.updateAttendance(attendanceId, request);
        SuccessDto<AttendanceResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }
}
