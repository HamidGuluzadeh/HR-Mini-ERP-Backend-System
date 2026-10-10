package com.hr.erp.employee.service;

import com.hr.erp.common.enums.EmployeeStatus;
import com.hr.erp.employee.dto.request.EmployeeRequest;
import com.hr.erp.employee.dto.response.EmployeeResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface EmployeeService {

    Page<EmployeeResponse> getAllEmployees(int page, int size);

    Page<EmployeeResponse> getEmployeesByDepartment(String departmentId, int page, int size);

    EmployeeResponse getEmployeeById(String employeeId);

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse updateEmployee(String employeeId, EmployeeRequest request);

    void changeEmployeeStatus(String employeeId, EmployeeStatus status);

    void terminateEmployee(String employeeId, LocalDate terminationDate);

}
