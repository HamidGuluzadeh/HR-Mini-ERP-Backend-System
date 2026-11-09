package com.hr.erp.leave.repository;

import com.hr.erp.leave.entity.LeaveRequestEntity;
import com.hr.erp.leave.model.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequestEntity, String> {

    Page<LeaveRequestEntity> findAllByStatus(LeaveStatus status, Pageable pageable);

    Page<LeaveRequestEntity> findAllByEmployeeId(String employeeId, Pageable pageable);

    @Query("""
            SELECT COUNT(l) > 0 FROM LeaveRequestEntity l
            WHERE l.employee.id = :employeeId
            AND l.status IN (:pendingStatus, :approvedStatus)
            AND (l.startDate <= :endDate AND l.endDate >= :startDate)
            """)
    boolean existsOverlappingLeave(
            @Param("employeeId") String employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("pendingStatus") LeaveStatus pendingStatus,
            @Param("approvedStatus") LeaveStatus approvedStatus
    );

    @Query("""
            SELECT COALESCE(SUM(l.totalDays), 0) FROM LeaveRequestEntity l
            WHERE l.employee.id = :employeeId
            AND l.leaveType.id = :leaveTypeId
            AND l.status = :status
            AND YEAR(l.startDate) = :year
            """)
    Integer countUsedDaysByEmployeeAndLeaveTypeInYear(
            @Param("employeeId") String employeeId,
            @Param("leaveTypeId") String leaveTypeId,
            @Param("status") LeaveStatus status,
            @Param("year") int year
    );

    @Query("""
        SELECT COALESCE(SUM(l.totalDays), 0) FROM LeaveRequestEntity l
        WHERE l.employee.id = :employeeId
        AND l.leaveType.isPaid = false
        AND l.status = :status
        AND (l.startDate <= :endDate AND l.endDate >= :startDate)
        """)
    Long sumUnpaidLeaveDaysInPeriod(
            @Param("employeeId") String employeeId,
            @Param("status") LeaveStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
