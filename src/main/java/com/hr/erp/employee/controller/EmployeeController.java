package com.hr.erp.employee.controller;

import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.employee.model.EmployeeStatus;
import com.hr.erp.common.enums.SuccessStatus;
import com.hr.erp.employee.dto.request.EmployeeRequest;
import com.hr.erp.employee.dto.response.EmployeeResponse;
import com.hr.erp.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/all")
    public ResponseEntity<SuccessDto<Page<EmployeeResponse>>> getAllEmployees(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "20") int size) {
        Page<EmployeeResponse> response = employeeService.getAllEmployees(page, size);
        SuccessDto<Page<EmployeeResponse>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/department/{departmentId}/all")
    public ResponseEntity<SuccessDto<Page<EmployeeResponse>>> getEmployeesByDepartment(@PathVariable String departmentId,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "20") int size) {
        Page<EmployeeResponse> response = employeeService.getEmployeesByDepartment(departmentId, page, size);
        SuccessDto<Page<EmployeeResponse>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<SuccessDto<EmployeeResponse>>  getEmployeeById(@PathVariable String employeeId) {
        EmployeeResponse response = employeeService.getEmployeeById(employeeId);
        SuccessDto<EmployeeResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<SuccessDto<EmployeeResponse>> createEmployee(@RequestBody @Valid EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        SuccessDto<EmployeeResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<SuccessDto<EmployeeResponse>> updateEmployee(@PathVariable String employeeId,
                                                                       @RequestBody @Valid EmployeeRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(employeeId, request);
        SuccessDto<EmployeeResponse> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PatchMapping("/{employeeId}/status")
    public ResponseEntity<Void> changeEmployeeStatus(@PathVariable String employeeId, @RequestParam EmployeeStatus status) {
        employeeService.changeEmployeeStatus(employeeId, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{employeeId}/termination")
    public ResponseEntity<Void> terminateEmployee(@PathVariable String employeeId,
                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate terminationDate) {
        employeeService.terminateEmployee(employeeId, terminationDate);
        return ResponseEntity.ok().build();
    }
}
