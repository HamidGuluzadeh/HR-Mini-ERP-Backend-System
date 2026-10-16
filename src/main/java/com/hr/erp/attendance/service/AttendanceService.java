package com.hr.erp.attendance.service;

import com.hr.erp.attendance.dto.request.AttendanceRequest;
import com.hr.erp.attendance.dto.response.AttendanceResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface AttendanceService {

    Page<AttendanceResponse> getAttendancesByEmployee(String employeeId, int page, int size);

    Page<AttendanceResponse> getAttendancesByDate(LocalDate workDate, int page, int size);

    Page<AttendanceResponse> getAttendancesByDateRange(LocalDate startDate, LocalDate endDate, int page, int size);

    AttendanceResponse getAttendanceById(String attendanceId);

    AttendanceResponse checkIn(String employeeId);

    AttendanceResponse checkOut(String employeeId);

    AttendanceResponse createAttendance(AttendanceRequest request);

    AttendanceResponse updateAttendance(String attendanceId, AttendanceRequest request);

}
