package com.hr.erp.leave.repository;

import com.hr.erp.leave.entity.LeaveTypeEntity;
import com.hr.erp.leave.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveTypeEntity, String> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, String id);

    LeaveTypeEntity findByName(String name);

}
