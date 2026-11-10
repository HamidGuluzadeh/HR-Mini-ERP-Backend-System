package com.hr.erp.payroll.controller;

import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.common.enums.SuccessStatus;
import com.hr.erp.payroll.dto.request.CreatePayrollRequestDto;
import com.hr.erp.payroll.dto.request.UpdatePayrollRequestDto;
import com.hr.erp.payroll.dto.response.PayrollResponseDto;
import com.hr.erp.payroll.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/payrolls")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollService payrollService;

    @GetMapping("/department/{departmentId}/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SuccessDto<Page<PayrollResponseDto>>> getPayrollsByDepartmentAndPeriod(@PathVariable String departmentId,
                                                                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
                                                                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd,
                                                                                                 @RequestParam(defaultValue = "0") int page,
                                                                                                 @RequestParam(defaultValue = "20") int size) {
        Page<PayrollResponseDto> response = payrollService.getPayrollsByDepartmentAndPeriod(departmentId, periodStart, periodEnd, page, size);
        SuccessDto<Page<PayrollResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/employee/{employeeId}/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SuccessDto<Page<PayrollResponseDto>>> getPayrollsByEmployeeId(@PathVariable String employeeId,
                                                                                        @RequestParam(defaultValue = "0") int page,
                                                                                        @RequestParam(defaultValue = "20") int size) {
        Page<PayrollResponseDto> response = payrollService.getPayrollsByEmployeeId(employeeId, page, size);
        SuccessDto<Page<PayrollResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{payrollId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SuccessDto<PayrollResponseDto>> getPayrollById(@PathVariable String payrollId) {
        PayrollResponseDto response = payrollService.getPayrollById(payrollId);
        SuccessDto<PayrollResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SuccessDto<PayrollResponseDto>> createIndividualPayroll(@RequestBody @Valid CreatePayrollRequestDto request) {
        PayrollResponseDto response = payrollService.createIndividualPayroll(request);
        SuccessDto<PayrollResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{payrollId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SuccessDto<PayrollResponseDto>> updatePayroll(@PathVariable String payrollId,
                                                                        @RequestBody @Valid UpdatePayrollRequestDto request) {
        PayrollResponseDto response = payrollService.updatePayroll(payrollId, request);
        SuccessDto<PayrollResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Void> createDepartmentPayrolls(@PathVariable String departmentId,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        payrollService.createDepartmentPayrolls(departmentId, periodStart, periodEnd);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/department/{departmentId}/approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Void> approveDepartmentPayrolls(@PathVariable String departmentId,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        payrollService.approveDepartmentPayrolls(departmentId, periodStart, periodEnd);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/department/{departmentId}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Void> executeDepartmentPayments(@PathVariable String departmentId,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        payrollService.executeDepartmentPayments(departmentId, periodStart, periodEnd);
        return ResponseEntity.ok().build();
    }
}
