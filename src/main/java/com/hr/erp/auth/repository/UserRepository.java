package com.hr.erp.auth.repository;

import com.hr.erp.auth.entity.User;
import com.hr.erp.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmployee(Employee employee);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmployeeCode(String employeeCode);

    @EntityGraph(attributePaths = {"employee"})
    List<User> findAllByIdIn(Collection<String> ids);

    @EntityGraph(attributePaths = {"employee"})
    Page<User> findAllByEmployee_Position_Department_Id(String departmentId, Pageable pageable);

    @EntityGraph(attributePaths = {"employee"})
    Page<User> findAllByEmployee_Position_Department_Code(String departmentCode, Pageable pageable);

}
