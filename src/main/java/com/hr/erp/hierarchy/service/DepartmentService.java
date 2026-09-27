package com.hr.erp.hierarchy.service;

import com.hr.erp.hierarchy.dto.DepartmentRequest;
import com.hr.erp.hierarchy.dto.DepartmentResponse;
import org.springframework.data.domain.Page;

public interface DepartmentService {

    Page<DepartmentResponse> getAllDepartments(int page, int size);

    DepartmentResponse getDepartmentById(String departmentId);

    DepartmentResponse createDepartment(DepartmentRequest request);

    DepartmentResponse updateDepartment(String id, DepartmentRequest request);

    void deleteDepartment(String departmentId);

}
