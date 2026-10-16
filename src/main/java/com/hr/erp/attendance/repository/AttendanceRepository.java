package com.hr.erp.attendance.repository;

import com.hr.erp.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {

    Page<Attendance> findAllByEmployeeId(String employeeId, Pageable pageable);

    Page<Attendance> findAllByWorkDate(LocalDate workDate, Pageable pageable);

    Page<Attendance> findAllByWorkDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    boolean existsByEmployeeIdAndWorkDate(String employeeId, LocalDate workDate);

    Optional<Attendance> findByEmployeeIdAndWorkDate(String employeeId, LocalDate workDate);

}
