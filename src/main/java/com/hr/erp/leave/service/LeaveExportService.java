package com.hr.erp.leave.service;

public interface LeaveExportService {

    long getUnpaidLeaveDays(String employeeId, int year, int month);

    int getRemainingPaidLeaveDays(String employeeId);

    long getLeaveDaysByTypeAndMonth(String employeeId, String leaveTypeName, int year, int month);

}
