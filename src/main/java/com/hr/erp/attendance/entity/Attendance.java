package com.hr.erp.attendance.entity;

import com.hr.erp.common.enums.AttendanceStatus;
import com.hr.erp.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    String id;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    Employee employee;

    @Column(name = "work_date", nullable = false)
    LocalDate workDate;

    @Column(name = "check_in", updatable = false)
    Instant checkIn;

    @Column(name = "check_out")
    Instant checkOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    AttendanceStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;

    @Column(name = "updated_by")
    String updatedBy;
}
