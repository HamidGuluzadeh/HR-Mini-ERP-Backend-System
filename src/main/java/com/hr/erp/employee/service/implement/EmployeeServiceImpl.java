package com.hr.erp.employee.service.implement;

import com.hr.erp.employee.model.EmployeeStatus;
import com.hr.erp.common.exception.BadRequestException;
import com.hr.erp.common.exception.ConflictException;
import com.hr.erp.common.exception.ResourceAlreadyExistsException;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.employee.dto.request.EmployeeRequest;
import com.hr.erp.employee.dto.response.EmployeeResponse;
import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.mapper.EmployeeMapper;
import com.hr.erp.employee.repository.EmployeeRepository;
import com.hr.erp.employee.service.EmployeeService;
import com.hr.erp.hierarchy.entity.Position;
import com.hr.erp.hierarchy.repository.DepartmentRepository;
import com.hr.erp.hierarchy.repository.PositionRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeServiceImpl implements EmployeeService {
    EmployeeRepository employeeRepository;
    PositionRepository positionRepository;
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

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (!Objects.isNull(request.terminationDate()) && request.terminationDate().isBefore(request.hireDate())) {
            throw new BadRequestException("Termination date cannot be before hire date!");
        }

        if (employeeRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new ResourceAlreadyExistsException("Employee with phone number " + request.phoneNumber() + " already exists!");
        }

        Position position = positionRepository.findById(request.positionId())
                .orElseThrow(() -> new ResourceNotFoundException("Position not found!"));

        Employee manager = null;

        if (!Objects.isNull(request.managerId())) {
            manager = employeeRepository.findById(request.managerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found!"));

            if (manager.getStatus() == EmployeeStatus.TERMINATED) {
                throw new BadRequestException("Terminated employee cannot be assigned as a manager!");
            }
        }

        Employee employee = employeeMapper.mapRequestToEntity(request);
        employee.setPosition(position);
        employee.setManager(manager);

        if (Objects.isNull(employee.getStatus())) {
            employee.setStatus(EmployeeStatus.ACTIVE);
        }

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.mapEntityToResponse(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(String employeeId, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        if (!Objects.isNull(request.terminationDate()) && request.terminationDate().isBefore(request.hireDate())) {
            throw new BadRequestException("Termination date cannot be before hire date!");
        }

        if (employeeRepository.existsByPhoneNumberAndIdNot(request.phoneNumber(), employeeId)) {
            throw new ResourceAlreadyExistsException("Phone number is already in use by another employee!");
        }

        Position position = positionRepository.findById(request.positionId())
                .orElseThrow(() -> new ResourceNotFoundException("Position not found!"));

        Employee manager = null;
        if (!Objects.isNull(request.managerId())) {
            if (request.managerId().equals(employeeId)) {
                throw new ConflictException("Employee cannot be their own manager!");
            }

            manager = employeeRepository.findById(request.managerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found!"));

            if (manager.getStatus() == EmployeeStatus.TERMINATED) {
                throw new BadRequestException("Terminated employee cannot be assigned as a manager!");
            }
        }

        employeeMapper.updateEntityFromRequest(request, employee);
        employee.setPosition(position);
        employee.setManager(manager);

        Employee updatedEmployee = employeeRepository.save(employee);

        return employeeMapper.mapEntityToResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public void changeEmployeeStatus(String employeeId, EmployeeStatus status) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        if (status == EmployeeStatus.TERMINATED && Objects.isNull(employee.getTerminationDate())) {
            employee.setTerminationDate(LocalDate.now());
        }

        employee.setStatus(status);
        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void terminateEmployee(String employeeId, LocalDate terminationDate) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        if (employee.getStatus() == EmployeeStatus.TERMINATED) {
            throw new BadRequestException("Employee is already terminated!");
        }

        if (terminationDate.isBefore(employee.getHireDate())) {
            throw new BadRequestException("Termination date cannot be before hire date!");
        }

        if (employeeRepository.existsByManagerId(employeeId)) {
            throw new ConflictException("Cannot terminate employee who is a manager for active subordinates. Reassign subordinates first!");
        }

        employee.setStatus(EmployeeStatus.TERMINATED);
        employee.setTerminationDate(terminationDate);

        employeeRepository.save(employee);
    }
}
