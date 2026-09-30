package com.hr.erp.hierarchy.repository;

import com.hr.erp.hierarchy.entity.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, String> {

    Page<Position> findAllByDepartmentId(String departmentId, Pageable pageable);

    boolean existsByTitleAndDepartmentId(String title, String departmentId);

    boolean existsByTitleAndDepartmentIdAndIdNot(String title, String departmentId, String id);

}
