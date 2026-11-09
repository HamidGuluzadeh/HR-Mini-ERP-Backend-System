package com.hr.erp.employee.repository;

import com.hr.erp.employee.entity.EmployeeContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeContractRepository extends JpaRepository<EmployeeContract, String> {

    Optional<EmployeeContract> findByEmployeeIdAndIsActiveTrue(String employeeId);

}
