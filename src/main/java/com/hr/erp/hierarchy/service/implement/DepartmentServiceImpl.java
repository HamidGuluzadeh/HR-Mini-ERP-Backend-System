package com.hr.erp.hierarchy.service.implement;

import com.hr.erp.common.exception.ConflictException;
import com.hr.erp.common.exception.ResourceAlreadyExistsException;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.hierarchy.dto.request.DepartmentRequest;
import com.hr.erp.hierarchy.dto.response.DepartmentResponse;
import com.hr.erp.hierarchy.entity.Department;
import com.hr.erp.hierarchy.mapper.DepartmentMapper;
import com.hr.erp.hierarchy.repository.DepartmentRepository;
import com.hr.erp.hierarchy.repository.PositionRepository;
import com.hr.erp.hierarchy.service.DepartmentService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentServiceImpl implements DepartmentService {
    DepartmentRepository departmentRepository;
    PositionRepository positionRepository;
    DepartmentMapper departmentMapper;

    @Override
    public Page<DepartmentResponse> getAllDepartments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Department> departments = departmentRepository.findAll(pageable);

        return departments.map(departmentMapper::mapEntityToResponse);
    }

    @Override
    public DepartmentResponse getDepartmentById(String departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found!"));

        return departmentMapper.mapEntityToResponse(department);
    }

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.code())) {
            throw new ResourceAlreadyExistsException("Department " + request.code() + " already exists!");
        }

        Department department = departmentMapper.mapRequestToEntity(request);

        if (!Objects.isNull(request.parentId())) {
            Department parent = departmentRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent department not found!"));

            department.setParent(parent);
        }

        Department savedDepartment = departmentRepository.save(department);

        return departmentMapper.mapEntityToResponse(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(String departmentId, DepartmentRequest request) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found!"));

        if (departmentRepository.existsByCodeAndIdNot(request.code(), departmentId)) {
            throw new ResourceAlreadyExistsException("Department code " + request.code() + " already in use!");
        }

        departmentMapper.updateEntityFromRequest(request, department);

        if (!Objects.isNull(request.parentId())) {
            if (request.parentId().equals(departmentId)) {
                throw new ConflictException("Department cannot be its own parent!");
            }

            Department parent = departmentRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent department not found!"));

            department.setParent(parent);
        } else {
            department.setParent(null);
        }

        Department updatedDepartment = departmentRepository.save(department);

        return departmentMapper.mapEntityToResponse(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(String departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found!");
        }

        if (departmentRepository.existsByParentId(departmentId)) {
            throw new ConflictException("Department contains sub-departments!");
        }

        if (positionRepository.existsByDepartmentId(departmentId)) {
            throw new ConflictException("Department has associated positions!");
        }

        departmentRepository.deleteById(departmentId);
    }
}
