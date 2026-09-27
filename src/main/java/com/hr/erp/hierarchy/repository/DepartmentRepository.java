package com.hr.erp.hierarchy.repository;

import com.hr.erp.hierarchy.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

    boolean existsByCode(String departmentCode);

    boolean existsByCodeAndIdNot(String departmentCode, String departmentId);

}
