package com.hr.erp.employee.repository;

import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.model.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    boolean existsByPositionId(String positionId);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, String employeeId);

    boolean existsByManagerId(String managerId);

    Page<Employee> findAllByPositionDepartmentId(String departmentId, Pageable pageable);

    Optional<Employee> findByCode(String employeeCode);

    List<Employee> findAllByPositionDepartmentIdAndStatus(String positionDepartmentId, EmployeeStatus status);

}
