package com.hr.erp.leave.service.implement;

import com.hr.erp.common.exception.ConflictException;
import com.hr.erp.common.exception.ResourceAlreadyExistsException;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.leave.dto.request.CreateLeaveTypeRequestDto;
import com.hr.erp.leave.dto.request.UpdateLeaveTypeRequestDto;
import com.hr.erp.leave.dto.response.LeaveTypeResponseDto;
import com.hr.erp.leave.entity.LeaveTypeEntity;
import com.hr.erp.leave.mapper.LeaveTypeMapper;
import com.hr.erp.leave.repository.LeaveTypeRepository;
import com.hr.erp.leave.service.LeaveTypeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {
    LeaveTypeRepository leaveTypeRepository;
    LeaveTypeMapper leaveTypeMapper;

    @Override
    public List<LeaveTypeResponseDto> getAllLeaveTypes() {
        List<LeaveTypeEntity> leaveTypeEntities = leaveTypeRepository.findAll();

        List<LeaveTypeResponseDto> responseDtoList = new ArrayList<>();

        for (LeaveTypeEntity leaveTypeEntity : leaveTypeEntities) {
            LeaveTypeResponseDto responseDto = leaveTypeMapper.mapEntityToResponse(leaveTypeEntity);
            responseDtoList.add(responseDto);
        }

        return responseDtoList;
    }

    @Override
    public LeaveTypeResponseDto getLeaveTypeById(String leaveTypeId) {
        LeaveTypeEntity leaveTypeEntity = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found!"));

        return leaveTypeMapper.mapEntityToResponse(leaveTypeEntity);
    }

    @Override
    @Transactional
    public LeaveTypeResponseDto createLeaveType(CreateLeaveTypeRequestDto request) {
        if (leaveTypeRepository.existsByNameIgnoreCase(request.name())) {
            throw new ResourceAlreadyExistsException(request.name() + " already exists!");
        }

        LeaveTypeEntity leaveTypeEntity = leaveTypeMapper.mapRequestToEntity(request);
        leaveTypeEntity.setCreatedAt(Instant.now());

        LeaveTypeEntity savedLeaveTypeEntity = leaveTypeRepository.save(leaveTypeEntity);

        return leaveTypeMapper.mapEntityToResponse(savedLeaveTypeEntity);
    }

    @Override
    @Transactional
    public LeaveTypeResponseDto updateLeaveType(String leaveTypeId, UpdateLeaveTypeRequestDto request) {
        LeaveTypeEntity leaveTypeEntity = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found!"));

        leaveTypeMapper.updateEntityFromRequest(request, leaveTypeEntity);
        leaveTypeEntity.setUpdatedAt(Instant.now());

        LeaveTypeEntity savedLeaveTypeEntity = leaveTypeRepository.save(leaveTypeEntity);

        return leaveTypeMapper.mapEntityToResponse(savedLeaveTypeEntity);
    }
}
