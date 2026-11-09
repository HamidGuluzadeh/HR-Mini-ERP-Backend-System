package com.hr.erp.leave.service.implement;

import com.hr.erp.leave.model.LeaveStatus;
import com.hr.erp.leave.repository.LeaveRequestRepository;
import com.hr.erp.leave.service.LeaveExportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeaveExportServiceImpl implements LeaveExportService {
    LeaveRequestRepository leaveRequestRepository;

    @Override
    public long getUnpaidLeaveDays(String employeeId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return leaveRequestRepository.sumUnpaidLeaveDaysInPeriod(
                employeeId, LeaveStatus.APPROVED, startDate, endDate
        );
    }

    @Override
    public int getRemainingPaidLeaveDays(String employeeId, String paidLeaveTypeId, int annualQuota) {
        int currentYear = LocalDate.now().getYear();

        Integer usedDays = leaveRequestRepository.countUsedDaysByEmployeeAndLeaveTypeInYear(
                employeeId, paidLeaveTypeId, LeaveStatus.APPROVED, currentYear
        );

        int totalUsed = usedDays != null ? usedDays : 0;

        return Math.max(0, annualQuota - totalUsed);
    }
}
