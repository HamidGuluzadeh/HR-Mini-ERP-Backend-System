package com.hr.erp.attendance.controller;

import com.hr.erp.attendance.dto.request.AttendanceRequest;
import com.hr.erp.attendance.dto.response.AttendanceResponse;
import com.hr.erp.attendance.service.AttendanceService;
import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.common.enums.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/employee/{employeeId}/all")
    public ResponseEntity<SuccessDto<Page<AttendanceResponse>>> getAttendancesByEmployee(@PathVariable String employeeId,
                                                                                         @RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "20") int size) {
        Page<AttendanceResponse> response = attendanceService.getAttendancesByEmployee(employeeId, page, size);
        SuccessDto<Page<AttendanceResponse>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/date")
    public ResponseEntity<SuccessDto<Page<AttendanceResponse>>> getAttendancesByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
                                                                                     @RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "20") int size) {
        Page<AttendanceResponse> response = attendanceService.getAttendancesByDate(workDate, page, size);
        SuccessDto<Page<AttendanceResponse>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<SuccessDto<Page<AttendanceResponse>>> getAttendancesByDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                                          @RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "20") int size) {
        Page<AttendanceResponse> response = attendanceService.getAttendancesByDateRange(startDate, endDate, page, size);
        SuccessDto<Page<AttendanceResponse>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{attendanceId}")
    public ResponseEntity<SuccessDto<AttendanceResponse>> getAttendanceById(@PathVariable String attendanceId) {
        AttendanceResponse response = attendanceService.getAttendanceById(attendanceId);
        SuccessDto<AttendanceResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/check-in/{employeeId}")
    public ResponseEntity<SuccessDto<AttendanceResponse>> checkIn(@PathVariable String employeeId) {
        AttendanceResponse response = attendanceService.checkIn(employeeId);
        SuccessDto<AttendanceResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/check-out/{employeeId}")
    public ResponseEntity<SuccessDto<AttendanceResponse>> checkOut(@PathVariable String employeeId) {
        AttendanceResponse response = attendanceService.checkOut(employeeId);
        SuccessDto<AttendanceResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PostMapping("/new")
    public ResponseEntity<SuccessDto<AttendanceResponse>> createAttendance(@RequestBody @Valid AttendanceRequest request) {
        AttendanceResponse response = attendanceService.createAttendance(request);
        SuccessDto<AttendanceResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{attendanceId}")
    public ResponseEntity<SuccessDto<AttendanceResponse>> updateAttendance(@PathVariable String attendanceId,
                                                                           @RequestBody @Valid  AttendanceRequest request) {
        AttendanceResponse response = attendanceService.updateAttendance(attendanceId, request);
        SuccessDto<AttendanceResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }
}
