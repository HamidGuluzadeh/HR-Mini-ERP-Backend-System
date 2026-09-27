package com.hr.erp.hierarchy.repository;

import com.hr.erp.hierarchy.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, String> {

}
