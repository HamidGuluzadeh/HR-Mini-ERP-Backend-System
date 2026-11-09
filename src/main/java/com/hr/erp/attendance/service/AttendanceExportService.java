package com.hr.erp.attendance.service;

import java.time.LocalDate;

public interface AttendanceExportService {

    long getUnexcusedAbsencesCount(String employeeId, LocalDate startDate, LocalDate endDate);

    long getLatePenaltiesCount(String employeeId, LocalDate startDate, LocalDate endDate);

}
