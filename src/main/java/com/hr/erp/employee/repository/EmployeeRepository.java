package com.hr.erp.employee.repository;

import com.hr.erp.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    boolean existsByPositionId(String positionId);

}
