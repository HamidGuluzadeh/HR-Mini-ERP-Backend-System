package com.hr.erp.payroll.entity;

import com.hr.erp.employee.entity.Employee;
import com.hr.erp.payroll.model.PayrollStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "payrolls")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    Employee employee;

    @Column(name = "period_start", nullable = false)
    LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    LocalDate periodEnd;

    @Column(name = "base_salary", nullable = false, precision = 6, scale = 2)
    BigDecimal baseSalary;

    @Column(name = "bonus", precision = 5, scale = 2)
    BigDecimal bonus;

    @Column(name = "bonus_reason", length = 500, nullable = false)
    String bonusReason;

    @Column(name = "deductions", precision = 6, scale = 2)
    BigDecimal deductions;

    @Column(name = "deduction_reason", length = 500, nullable = false)
    String deductionReason;

    @Column(name = "net_salary", nullable = false, precision = 6, scale = 2)
    BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    PayrollStatus status;

    @CreationTimestamp
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
