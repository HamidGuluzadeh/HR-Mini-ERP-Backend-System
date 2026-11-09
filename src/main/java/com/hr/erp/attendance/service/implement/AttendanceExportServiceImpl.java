package com.hr.erp.attendance.service.implement;

import com.hr.erp.attendance.model.AttendanceStatus;
import com.hr.erp.attendance.repository.AttendanceRepository;
import com.hr.erp.attendance.service.AttendanceExportService;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.employee.repository.EmployeeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceExportServiceImpl implements AttendanceExportService {
    AttendanceRepository attendanceRepository;
    EmployeeRepository employeeRepository;

    @Override
    public long getUnexcusedAbsencesCount(String employeeId, LocalDate startDate, LocalDate endDate) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        return attendanceRepository.countByEmployeeIdAndStatusAndWorkDateBetween(employeeId,
                AttendanceStatus.ABSENT, startDate, endDate);
    }

    @Override
    public long getLatePenaltiesCount(String employeeId, LocalDate startDate, LocalDate endDate) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        return attendanceRepository.countByEmployeeIdAndStatusAndWorkDateBetween(employeeId,
                AttendanceStatus.LATE, startDate, endDate);
    }
}
