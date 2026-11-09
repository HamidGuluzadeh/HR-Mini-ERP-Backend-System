package com.hr.erp.leave.service;

public interface LeaveExportService {

    long getUnpaidLeaveDays(String employeeId, int year, int month);

    int getRemainingPaidLeaveDays(String employeeId, String paidLeaveTypeId, int annualQuota);

}
