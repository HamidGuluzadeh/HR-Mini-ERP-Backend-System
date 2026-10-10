package com.hr.erp.employee.service.implement;

import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.employee.dto.response.EmployeeResponse;
import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.mapper.EmployeeMapper;
import com.hr.erp.employee.repository.EmployeeRepository;
import com.hr.erp.employee.service.EmployeeService;
import com.hr.erp.hierarchy.repository.DepartmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeServiceImpl implements EmployeeService {
    EmployeeRepository employeeRepository;
    DepartmentRepository departmentRepository;
    EmployeeMapper employeeMapper;

    @Override
    public Page<EmployeeResponse> getAllEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Employee> employees = employeeRepository.findAll(pageable);

        return employees.map(employeeMapper::mapEntityToResponse);
    }

    @Override
    public Page<EmployeeResponse> getEmployeesByDepartment(String departmentId, int page, int size) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found!");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Employee> employees = employeeRepository.findAllByPositionDepartmentId(departmentId, pageable);

        return employees.map(employeeMapper::mapEntityToResponse);
    }

    @Override
    public EmployeeResponse getEmployeeById(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        return employeeMapper.mapEntityToResponse(employee);
    }
}
