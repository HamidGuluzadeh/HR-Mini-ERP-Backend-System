package com.hr.erp.hierarchy.service.implement;

import com.hr.erp.common.exception.BadRequestException;
import com.hr.erp.common.exception.ResourceAlreadyExistsException;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.hierarchy.dto.request.PositionRequest;
import com.hr.erp.hierarchy.dto.response.PositionResponse;
import com.hr.erp.hierarchy.entity.Department;
import com.hr.erp.hierarchy.entity.Position;
import com.hr.erp.hierarchy.mapper.PositionMapper;
import com.hr.erp.hierarchy.repository.DepartmentRepository;
import com.hr.erp.hierarchy.repository.PositionRepository;
import com.hr.erp.hierarchy.service.PositionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PositionServiceImpl implements PositionService {
    DepartmentRepository departmentRepository;
    PositionRepository positionRepository;
    PositionMapper positionMapper;

    @Override
    public Page<PositionResponse> getAllPositions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Position> positions = positionRepository.findAll(pageable);

        return positions.map(positionMapper::mapEntityToResponse);
    }

    @Override
    public Page<PositionResponse> getPositionsByDepartment(String departmentId, int page, int size) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found!");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Position> positions = positionRepository.findAllByDepartmentId(departmentId, pageable);

        return positions.map(positionMapper::mapEntityToResponse);
    }

    @Override
    public PositionResponse getPositionById(String positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found!"));

        return positionMapper.mapEntityToResponse(position);
    }

    @Override
    @Transactional
    public PositionResponse createPosition(PositionRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found!"));

        if (positionRepository.existsByTitleAndDepartmentId(request.title(), request.departmentId())) {
            throw new ResourceAlreadyExistsException(request.title() + " position already exists in "
                    + department.getName() + " department!");
        }

        if (request.minSalary().compareTo(request.maxSalary()) > 0) {
            throw new BadRequestException("Minimum salary cannot be greater than maximum salary!");
        }

        Position position = positionMapper.mapRequestToEntity(request);
        position.setDepartment(department);

        Position savedPosition = positionRepository.save(position);

        return positionMapper.mapEntityToResponse(savedPosition);
    }

    @Override
    @Transactional
    public PositionResponse updatePosition(String positionId, PositionRequest request) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found!"));

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found!"));

        if (positionRepository.existsByTitleAndDepartmentIdAndIdNot(request.title(), request.departmentId(), positionId)) {
            throw new ResourceAlreadyExistsException(request.title() + " position already exists in "
                + department.getName() + " department!");
        }

        if (request.minSalary().compareTo(request.maxSalary()) > 0) {
            throw new BadRequestException("Minimum salary cannot be greater than maximum salary!");
        }

        positionMapper.updateEntityFromRequest(request, position);
        position.setDepartment(department);

        Position updatedPosition = positionRepository.save(position);

        return positionMapper.mapEntityToResponse(updatedPosition);
    }

    @Override
    @Transactional
    public void deletePosition(String positionId) {
        if (!positionRepository.existsById(positionId)) {
            throw new ResourceNotFoundException("Position not found!");
        }

        positionRepository.deleteById(positionId);
    }
}
