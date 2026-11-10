package com.hr.erp.payroll.service.implement;

import com.hr.erp.attendance.service.AttendanceExportService;
import com.hr.erp.common.exception.BadRequestException;
import com.hr.erp.common.exception.ConflictException;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.entity.EmployeeContract;
import com.hr.erp.employee.model.EmployeeStatus;
import com.hr.erp.employee.model.SalaryType;
import com.hr.erp.employee.repository.EmployeeContractRepository;
import com.hr.erp.employee.repository.EmployeeRepository;
import com.hr.erp.hierarchy.repository.DepartmentRepository;
import com.hr.erp.leave.model.LeaveType;
import com.hr.erp.leave.service.LeaveExportService;
import com.hr.erp.payroll.dto.request.CreatePayrollRequestDto;
import com.hr.erp.payroll.dto.request.UpdatePayrollRequestDto;
import com.hr.erp.payroll.dto.response.PayrollResponseDto;
import com.hr.erp.payroll.entity.Payroll;
import com.hr.erp.payroll.mapper.PayrollMapper;
import com.hr.erp.payroll.model.PayrollStatus;
import com.hr.erp.payroll.repository.PayrollRepository;
import com.hr.erp.payroll.service.PayrollService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PayrollServiceImpl implements PayrollService {
    PayrollRepository payrollRepository;
    EmployeeRepository employeeRepository;
    EmployeeContractRepository contractRepository;
    DepartmentRepository departmentRepository;
    AttendanceExportService attendanceExportService;
    LeaveExportService leaveExportService;
    PayrollMapper payrollMapper;

    static int DAILY_WORKING_HOURS = 9;

    @Override
    public Page<PayrollResponseDto> getPayrollsByDepartmentAndPeriod(String departmentId, LocalDate periodStart,
                                                                     LocalDate periodEnd, int page, int size) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found!");
        }

        Page<Payroll> payrolls = payrollRepository.findByEmployeePosition_DepartmentIdAndPeriodStartAndPeriodEnd(
                departmentId, periodStart, periodEnd, PageRequest.of(page, size)
        );

        return payrolls.map(payrollMapper::mapEntityToResponse);
    }

    @Override
    public Page<PayrollResponseDto> getPayrollsByEmployeeId(String employeeId, int page, int size) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        Page<Payroll> payrolls = payrollRepository.findByEmployeeIdOrderByPeriodStartDesc(
                employeeId, PageRequest.of(page, size)
        );

        return payrolls.map(payrollMapper::mapEntityToResponse);
    }

    @Override
    public PayrollResponseDto getPayrollById(String payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found!"));

        return payrollMapper.mapEntityToResponse(payroll);
    }

    @Override
    @Transactional
    public PayrollResponseDto createIndividualPayroll(CreatePayrollRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = !Objects.isNull(auth) ? auth.getName() : "SYSTEM";

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        if (payrollRepository.existsByEmployeeIdAndPeriodStartAndPeriodEnd(employee.getId(), request.periodStart(), request.periodEnd())) {
            throw new ConflictException("Payroll already exists for this period!");
        }

        Payroll payroll = calculatePayroll(employee, request.periodStart(), request.periodEnd(), request.bonus(),
                request.bonusReason(), request.deductions(), request.deductionReason());

        payroll.setCreatedAt(Instant.now());
        payroll.setCreatedBy(userId);

        Payroll savedPayroll = payrollRepository.save(payroll);

        return payrollMapper.mapEntityToResponse(savedPayroll);
    }

    @Override
    @Transactional
    public PayrollResponseDto updatePayroll(String payrollId, UpdatePayrollRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = !Objects.isNull(auth) ? auth.getName() : "SYSTEM";

        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found!"));

        if (!payroll.getStatus().equals(PayrollStatus.DRAFT)) {
            throw new BadRequestException("Only DRAFT payrolls can be updated!");
        }

        BigDecimal base = payroll.getBaseSalary();

        Payroll recalculatedPayroll = calculatePayroll(payroll.getEmployee(), payroll.getPeriodStart(), payroll.getPeriodEnd(),
                request.bonus(), request.bonusReason(),
                request.deductions(), request.deductionReason());

        payroll.setBonus(recalculatedPayroll.getBonus());
        payroll.setBonusReason(recalculatedPayroll.getBonusReason());
        payroll.setDeductions(recalculatedPayroll.getDeductions());
        payroll.setDeductionReason(recalculatedPayroll.getDeductionReason());
        payroll.setNetSalary(recalculatedPayroll.getNetSalary());
        payroll.setUpdatedAt(Instant.now());
        payroll.setUpdatedBy(userId);

        Payroll savedPayroll = payrollRepository.save(payroll);

        return payrollMapper.mapEntityToResponse(savedPayroll);
    }

    @Override
    @Transactional
    public void createDepartmentPayrolls(String departmentId, LocalDate periodStart, LocalDate periodEnd) {
        List<Employee> employees = employeeRepository.findAllByPositionDepartmentIdAndStatus(departmentId, EmployeeStatus.ACTIVE);

        List<Payroll> payrolls = employees.stream()
                .filter(emp -> !payrollRepository.existsByEmployeeIdAndPeriodStartAndPeriodEnd(emp.getId(), periodStart, periodEnd))
                .map(emp -> calculatePayroll(
                        emp, periodStart, periodEnd, BigDecimal.ZERO, null, BigDecimal.ZERO, null
                ))
                .toList();

        payrollRepository.saveAll(payrolls);
    }

    @Override
    @Transactional
    public void approveDepartmentPayrolls(String departmentId, LocalDate periodStart, LocalDate periodEnd) {
        List<Payroll> payrolls = payrollRepository.findByEmployeePositionDepartmentIdAndPeriodStartAndPeriodEndAndStatus(
                departmentId, periodStart, periodEnd, PayrollStatus.DRAFT
        );

        payrolls.forEach(payroll -> payroll.setStatus(PayrollStatus.APPROVED));

        payrollRepository.saveAll(payrolls);
    }

    @Override
    @Transactional
    public void executeDepartmentPayments(String departmentId, LocalDate periodStart, LocalDate periodEnd) {
        List<Payroll> approvedPayrolls = payrollRepository.findByEmployeePositionDepartmentIdAndPeriodStartAndPeriodEndAndStatus(
                departmentId, periodStart, periodEnd, PayrollStatus.APPROVED
        );

        approvedPayrolls.forEach(payroll -> payroll.setStatus(PayrollStatus.PAID));

        payrollRepository.saveAll(approvedPayrolls);
    }

    private Payroll calculatePayroll(Employee employee, LocalDate periodStart, LocalDate periodEnd,
                                     BigDecimal manualBonus, String bonusReason,
                                     BigDecimal manualDeductions, String deductionReason) {

        EmployeeContract contract = contractRepository.findByEmployeeIdAndIsActiveTrue(employee.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Any active contract not found for employee!"));

        BigDecimal baseSalary = contract.getBaseSalary();

        if (Objects.isNull(baseSalary)) {
            throw new BadRequestException("Employee has no fixed salary!");
        }

        int daysInMonth = YearMonth.from(periodStart).lengthOfMonth();

        BigDecimal dailyRate = SalaryType.FIXED.equals(contract.getSalaryType())
                ? baseSalary.divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP)
                : baseSalary.multiply(BigDecimal.valueOf(DAILY_WORKING_HOURS));

        StringBuilder dedReasonBuilder = new StringBuilder(deductionReason != null ? deductionReason : "");
        BigDecimal totalDeductions = calculateTotalDeductions(contract, employee, periodStart,
                periodEnd, dailyRate, manualDeductions, dedReasonBuilder);
        String finalDeductionReason = dedReasonBuilder.isEmpty() ? null : dedReasonBuilder.toString();

        StringBuilder bonusReasonBuilder = new StringBuilder(bonusReason != null ? bonusReason : "");
        BigDecimal totalBonus = calculateTotalBonus(employee, periodStart, periodEnd, dailyRate, manualBonus, bonusReasonBuilder);
        String finalBonusReason = bonusReasonBuilder.isEmpty() ? null : bonusReasonBuilder.toString();

        BigDecimal netSalary = calculateNetSalary(baseSalary, totalDeductions, totalBonus);

        return buildPayrollEntity(employee, periodStart, periodEnd, baseSalary,
                totalBonus, finalBonusReason, totalDeductions, finalDeductionReason, netSalary);
    }

    private BigDecimal calculateNetSalary(BigDecimal baseSalary, BigDecimal totalDeductions, BigDecimal totalBonus) {
        BigDecimal netSalary = baseSalary.subtract(totalDeductions).add(totalBonus);

        if (netSalary.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return netSalary;
    }

    private BigDecimal calculateTotalBonus(Employee employee, LocalDate periodStart, LocalDate periodEnd,
                                           BigDecimal dailyRate, BigDecimal manualBonus,
                                           StringBuilder bonusReason) {

        BigDecimal totalBonus = !Objects.isNull(manualBonus) ? manualBonus : BigDecimal.ZERO;

        if (!Objects.isNull(employee.getTerminationDate()) &&
                !employee.getTerminationDate().isAfter(periodEnd) &&
                !employee.getTerminationDate().isBefore(periodStart)) {

            int remainingLeave = leaveExportService.getRemainingPaidLeaveDays(employee.getId());
            totalBonus = totalBonus.add(dailyRate.multiply(BigDecimal.valueOf(remainingLeave)));

            if (bonusReason.isEmpty()) {
                bonusReason.append("Final Settlement Leave Compensation");
            }
        }

        return totalBonus;
    }

    private BigDecimal calculateTotalDeductions(EmployeeContract contract, Employee employee,
                                                LocalDate periodStart, LocalDate periodEnd,
                                                BigDecimal dailyRate, BigDecimal manualDeductions, StringBuilder deductionReason) {

        BigDecimal absenceDeduction = calculateAbsenceDeduction(contract, employee.getId(), periodStart, periodEnd, dailyRate);
        BigDecimal leaveDeduction = calculateLeaveDeductions(employee.getId(), periodStart, dailyRate);
        BigDecimal lateDeduction = calculateLateDeduction(employee.getId(), periodStart, periodEnd, dailyRate);
        BigDecimal baseManualDeductions = !Objects.isNull(manualDeductions) ? manualDeductions : BigDecimal.ZERO;

        if (deductionReason.isEmpty()) {
            List<String> reasons = new ArrayList<>();

            if (absenceDeduction.compareTo(BigDecimal.ZERO) > 0) {
                reasons.add("Absence");
            } if (leaveDeduction.compareTo(BigDecimal.ZERO) > 0) {
                reasons.add("Unpaid/Sick Leave");
            } if (lateDeduction.compareTo(BigDecimal.ZERO) > 0) {
                reasons.add("Late Penalty");
            }

            deductionReason.append("Deductions: ").append(String.join(", ", reasons));
        }

        return baseManualDeductions.add(absenceDeduction).add(leaveDeduction).add(lateDeduction);
    }

    private BigDecimal calculateLeaveDeductions(String employeeId, LocalDate periodStart, BigDecimal dailyRate) {
        int year = periodStart.getYear();
        int month = periodStart.getMonthValue();

        BigDecimal totalLeaveDeduction = BigDecimal.ZERO;

        long unpaidLeaveDays = leaveExportService.getUnpaidLeaveDays(employeeId, year, month);

        if (unpaidLeaveDays > 0) {
            BigDecimal unpaidDeduction = dailyRate.multiply(BigDecimal.valueOf(unpaidLeaveDays));
            totalLeaveDeduction = totalLeaveDeduction.add(unpaidDeduction);
        }

        long sickLeaveDays = leaveExportService.getLeaveDaysByTypeAndMonth(
                employeeId, String.valueOf(LeaveType.SICK), year, month
        );

        if (sickLeaveDays > 14) {
            long deductibleSickDays = sickLeaveDays - 14;

            BigDecimal sickDeduction = dailyRate.multiply(BigDecimal.valueOf(deductibleSickDays));
            totalLeaveDeduction = totalLeaveDeduction.add(sickDeduction);
        }

        return totalLeaveDeduction;
    }

    private BigDecimal calculateAbsenceDeduction(EmployeeContract contract, String employeeId,
                                                 LocalDate periodStart, LocalDate periodEnd, BigDecimal dailyRate) {

        long absences = attendanceExportService.getAbsencesCount(employeeId, periodStart, periodEnd);

        if (absences == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal baseSalary = contract.getBaseSalary() != null ? contract.getBaseSalary() : BigDecimal.ZERO;
        BigDecimal totalAbsenceDeduction = BigDecimal.ZERO;

        if (SalaryType.FIXED.equals(contract.getSalaryType())) {
            BigDecimal baseAbsenceLoss = dailyRate.multiply(BigDecimal.valueOf(absences));
            totalAbsenceDeduction = totalAbsenceDeduction.add(baseAbsenceLoss);
        }

        else if (SalaryType.HOURLY.equals(contract.getSalaryType())) {
            BigDecimal dailyLossForHourly = baseSalary.multiply(BigDecimal.valueOf(DAILY_WORKING_HOURS))
                    .multiply(BigDecimal.valueOf(absences));

            totalAbsenceDeduction = totalAbsenceDeduction.add(dailyLossForHourly);
        }

        BigDecimal penaltyMultiplier = new BigDecimal("1.0");
        BigDecimal absencePenalty = dailyRate.multiply(penaltyMultiplier).multiply(BigDecimal.valueOf(absences));

        totalAbsenceDeduction = totalAbsenceDeduction.add(absencePenalty);

        return totalAbsenceDeduction;
    }

    private BigDecimal calculateLateDeduction(String employeeId, LocalDate periodStart,
                                              LocalDate periodEnd, BigDecimal dailyRate) {

        long latePenaltiesCount = attendanceExportService.getLatePenaltiesCount(employeeId, periodStart, periodEnd);

        if (latePenaltiesCount == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal latePenaltyMultiplier = new BigDecimal("0.25");

        return dailyRate.multiply(latePenaltyMultiplier).multiply(BigDecimal.valueOf(latePenaltiesCount));
    }

    private Payroll buildPayrollEntity(Employee employee, LocalDate periodStart, LocalDate periodEnd,
                                       BigDecimal baseSalary, BigDecimal totalBonus, String bonusReason,
                                       BigDecimal totalDeductions, String deductionReason, BigDecimal netSalary) {
        return Payroll.builder()
                .employee(employee)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .baseSalary(baseSalary)
                .bonus(totalBonus)
                .bonusReason(bonusReason)
                .deductions(totalDeductions)
                .deductionReason(deductionReason)
                .netSalary(netSalary)
                .status(PayrollStatus.DRAFT)
                .build();
    }
}
