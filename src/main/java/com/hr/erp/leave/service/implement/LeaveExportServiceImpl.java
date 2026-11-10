package com.hr.erp.leave.service.implement;

import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.employee.repository.EmployeeRepository;
import com.hr.erp.leave.entity.LeaveTypeEntity;
import com.hr.erp.leave.model.LeaveStatus;
import com.hr.erp.leave.model.LeaveType;
import com.hr.erp.leave.repository.LeaveRequestRepository;
import com.hr.erp.leave.repository.LeaveTypeRepository;
import com.hr.erp.leave.service.LeaveExportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeaveExportServiceImpl implements LeaveExportService {
    LeaveRequestRepository leaveRequestRepository;
    LeaveTypeRepository leaveTypeRepository;
    EmployeeRepository employeeRepository;

    @Override
    public long getUnpaidLeaveDays(String employeeId, int year, int month) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        Long unpaidDays =  leaveRequestRepository.sumUnpaidLeaveDaysInPeriod(
                employeeId, LeaveStatus.APPROVED, startDate, endDate
        );

        return !Objects.isNull(unpaidDays) ? unpaidDays : 0L;
    }

    @Override
    public int getRemainingPaidLeaveDays(String employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        LeaveTypeEntity leaveType = leaveTypeRepository.findByName(String.valueOf(LeaveType.ANNUAL));

        String leaveTypeId = leaveType.getId();
        int quota = leaveType.getMaxDaysPerYear();

        int currentYear = LocalDate.now().getYear();

        Integer usedDays = leaveRequestRepository.countUsedDaysByEmployeeAndLeaveTypeInYear(
                employeeId, leaveTypeId, LeaveStatus.APPROVED, currentYear
        );

        int totalUsed = !Objects.isNull(usedDays) ? usedDays : 0;

        return Math.max(0, quota - totalUsed);
    }
}
