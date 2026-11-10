package com.hr.erp.payroll.service;

import com.hr.erp.payroll.dto.request.CreatePayrollRequestDto;
import com.hr.erp.payroll.dto.request.UpdatePayrollRequestDto;
import com.hr.erp.payroll.dto.response.PayrollResponseDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface PayrollService {

    Page<PayrollResponseDto> getPayrollsByDepartmentAndPeriod(String departmentId, LocalDate periodStart,
                                                              LocalDate periodEnd, int page, int size);

    Page<PayrollResponseDto> getPayrollsByEmployeeId(String employeeId, int page, int size);

    PayrollResponseDto getPayrollById(String payrollId);

    PayrollResponseDto createIndividualPayroll(CreatePayrollRequestDto request);

    PayrollResponseDto updatePayroll(String payrollId, UpdatePayrollRequestDto request);

    void createDepartmentPayrolls(String departmentId, LocalDate periodStart, LocalDate periodEnd);

    void approveDepartmentPayrolls(String departmentId, LocalDate periodStart, LocalDate periodEnd);

    void executeDepartmentPayments(String departmentId, LocalDate periodStart, LocalDate periodEnd);

}
