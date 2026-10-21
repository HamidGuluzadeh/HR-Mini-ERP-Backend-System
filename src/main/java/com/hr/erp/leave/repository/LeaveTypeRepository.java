package com.hr.erp.leave.repository;

import com.hr.erp.leave.entity.LeaveTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveTypeEntity, String> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, String id);

}
