package com.hr.erp.employee.service.implement;

import com.hr.erp.employee.dto.request.ChangeSalaryRequest;
import com.hr.erp.employee.entity.EmployeeContract;
import com.hr.erp.employee.model.EmployeeStatus;
import com.hr.erp.common.exception.BadRequestException;
import com.hr.erp.common.exception.ConflictException;
import com.hr.erp.common.exception.ResourceAlreadyExistsException;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.employee.dto.request.EmployeeRequest;
import com.hr.erp.employee.dto.response.EmployeeResponse;
import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.mapper.EmployeeMapper;
import com.hr.erp.employee.repository.EmployeeContractRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeServiceImpl implements EmployeeService {
    EmployeeRepository employeeRepository;
    PositionRepository positionRepository;
    DepartmentRepository departmentRepository;
    EmployeeContractRepository contractRepository;
    EmployeeMapper employeeMapper;

    @Override
    public Page<EmployeeResponse> getEmployeesByDepartment(String departmentId, int page, int size) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found!");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Employee> employees = employeeRepository.findAllByPositionDepartmentId(departmentId, pageable);

        return employees.map(employee -> {
            EmployeeContract contract = contractRepository.findByEmployeeIdAndIsActiveTrue(employee.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Any active contract not found for employee!"));

            return employeeMapper.mapEntityToResponse(employee, contract);
        });
    }

    @Override
    public EmployeeResponse getEmployeeById(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        EmployeeContract contract = contractRepository.findByEmployeeIdAndIsActiveTrue(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Any active contract not found for employee!"));

        return employeeMapper.mapEntityToResponse(employee, contract);
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "SYSTEM";

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

        employee.setCreatedAt(Instant.now());
        employee.setCreatedBy(userId);

        Employee savedEmployee = employeeRepository.save(employee);

        EmployeeContract contract = EmployeeContract.builder()
                .employee(savedEmployee)
                .salaryType(request.salaryType())
                .baseSalary(request.baseSalary())
                .hourlyRate(request.hourlyRate())
                .effectiveFrom(request.hireDate())
                .effectiveTo(null)
                .isActive(true)
                .build();

        contractRepository.save(contract);

        return employeeMapper.mapEntityToResponse(savedEmployee, contract);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(String employeeId, EmployeeRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "SYSTEM";

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        EmployeeContract contract = contractRepository.findByEmployeeIdAndIsActiveTrue(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Any active contract not found for employee!"));

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
        employee.setUpdatedAt(Instant.now());
        employee.setUpdatedBy(userId);

        Employee updatedEmployee = employeeRepository.save(employee);



        return employeeMapper.mapEntityToResponse(updatedEmployee, contract);
    }

    @Override
    @Transactional
    public void changeSalary(String employeeId, ChangeSalaryRequest request) {
        EmployeeContract currentContract = contractRepository.findByEmployeeIdAndIsActiveTrue(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Any active contract not found for employee!"));

        if (request.effectiveFrom().isBefore(currentContract.getEffectiveFrom()) ||
                request.effectiveFrom().isEqual(currentContract.getEffectiveFrom())) {
            throw new BadRequestException("New contract's effective date must be after the current contract's start date!");
        }

        currentContract.setEffectiveTo(request.effectiveFrom().minusDays(1));
        currentContract.setIsActive(false);
        contractRepository.save(currentContract);

        EmployeeContract newContract = EmployeeContract.builder()
                .employee(currentContract.getEmployee())
                .salaryType(request.salaryType())
                .baseSalary(request.baseSalary())
                .hourlyRate(request.hourlyRate())
                .effectiveFrom(request.effectiveFrom())
                .effectiveTo(null)
                .isActive(true)
                .build();

        contractRepository.save(newContract);
    }

    @Override
    @Transactional
    public void changeEmployeeStatus(String employeeId, EmployeeStatus status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "SYSTEM";

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        if (status == EmployeeStatus.TERMINATED && Objects.isNull(employee.getTerminationDate())) {
            employee.setTerminationDate(LocalDate.now());
        }

        employee.setStatus(status);
        employee.setUpdatedAt(Instant.now());
        employee.setUpdatedBy(userId);

        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void terminateEmployee(String employeeId, LocalDate terminationDate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "SYSTEM";

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
        employee.setUpdatedAt(Instant.now());
        employee.setUpdatedBy(userId);

        employeeRepository.save(employee);
    }
}
