package com.hr.erp.payroll.repository;

import com.hr.erp.payroll.entity.Payroll;
import com.hr.erp.payroll.model.PayrollStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, String> {

    Page<Payroll> findByEmployeePosition_DepartmentIdAndPeriodStartAndPeriodEnd(
            String departmentId, LocalDate periodStart, LocalDate periodEnd, Pageable pageable
    );

    Page<Payroll> findByEmployeeIdOrderByPeriodStartDesc(String employeeId, Pageable pageable);

    boolean existsByEmployeeIdAndPeriodStartAndPeriodEnd(String employeeId, LocalDate periodStart, LocalDate periodEnd);

    List<Payroll> findByEmployeePositionDepartmentIdAndPeriodStartAndPeriodEndAndStatus(String departmentId, LocalDate periodStart, LocalDate periodEnd, PayrollStatus payrollStatus);

}
