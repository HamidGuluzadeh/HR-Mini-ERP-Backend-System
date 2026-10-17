package com.hr.erp.leave.repository;

import com.hr.erp.leave.entity.LeaveRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequestEntity, String> {

}
