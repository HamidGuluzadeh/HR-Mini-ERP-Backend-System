package com.hr.erp.employee.entity;

import com.hr.erp.employee.model.SalaryType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "employee_contracts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeContract {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, unique = true)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "salary_type", length = 20, nullable = false)
    SalaryType salaryType;

    @Column(name = "base_salary", precision = 8, scale = 2)
    BigDecimal baseSalary;

    @Column(name = "hourly_rate", precision = 6, scale = 2)
    BigDecimal hourlyRate;

    @Column(name = "effective_from", nullable = false)
    LocalDate effectiveFrom;

    @Column(name = "effective_to")
    LocalDate effectiveTo;

    @Column(name = "is_active", nullable = false)
    Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @Column(name = "created_by", length = 36, nullable = false, updatable = false)
    String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;

    @Column(name = "updated_by", length = 36)
    String updatedBy;
}