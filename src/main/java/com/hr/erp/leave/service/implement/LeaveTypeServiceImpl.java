package com.hr.erp.leave.service.implement;

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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "SYSTEM";

        if (leaveTypeRepository.existsByNameIgnoreCase(request.name())) {
            throw new ResourceAlreadyExistsException(request.name() + " already exists!");
        }

        LeaveTypeEntity leaveTypeEntity = leaveTypeMapper.mapRequestToEntity(request);
        leaveTypeEntity.setCreatedAt(Instant.now());
        leaveTypeEntity.setCreatedBy(userId);

        LeaveTypeEntity savedLeaveTypeEntity = leaveTypeRepository.save(leaveTypeEntity);

        return leaveTypeMapper.mapEntityToResponse(savedLeaveTypeEntity);
    }

    @Override
    @Transactional
    public LeaveTypeResponseDto updateLeaveType(String leaveTypeId, UpdateLeaveTypeRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "SYSTEM";

        LeaveTypeEntity leaveTypeEntity = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found!"));

        leaveTypeMapper.updateEntityFromRequest(request, leaveTypeEntity);
        leaveTypeEntity.setUpdatedAt(Instant.now());
        leaveTypeEntity.setUpdatedBy(userId);

        LeaveTypeEntity savedLeaveTypeEntity = leaveTypeRepository.save(leaveTypeEntity);

        return leaveTypeMapper.mapEntityToResponse(savedLeaveTypeEntity);
    }
}
