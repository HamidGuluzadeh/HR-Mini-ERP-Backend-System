package com.hr.erp.leave.service.implement;

import com.hr.erp.common.exception.BadRequestException;
import com.hr.erp.common.exception.ConflictException;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.model.EmployeeStatus;
import com.hr.erp.employee.repository.EmployeeRepository;
import com.hr.erp.leave.dto.request.CreateLeaveRequestDto;
import com.hr.erp.leave.dto.response.LeaveResponseDto;
import com.hr.erp.leave.entity.LeaveRequestEntity;
import com.hr.erp.leave.entity.LeaveTypeEntity;
import com.hr.erp.leave.mapper.LeaveRequestMapper;
import com.hr.erp.leave.model.LeaveStatus;
import com.hr.erp.leave.repository.LeaveRequestRepository;
import com.hr.erp.leave.repository.LeaveTypeRepository;
import com.hr.erp.leave.service.LeaveRequestService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeaveRequestServiceImpl implements LeaveRequestService {
    LeaveRequestRepository leaveRequestRepository;
    LeaveTypeRepository leaveTypeRepository;
    EmployeeRepository employeeRepository;
    LeaveRequestMapper leaveRequestMapper;

    static ZoneId COMPANY_ZONE_ID = ZoneId.of("Asia/Baku");

    @Override
    public Page<LeaveResponseDto> getLeaveRequestsByStatus(LeaveStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<LeaveRequestEntity> leaveRequestEntities = leaveRequestRepository.findAllByStatus(status, pageable);

        return leaveRequestEntities.map(leaveRequestMapper::mapEntityToResponse);
    }

    @Override
    public Page<LeaveResponseDto> getLeaveRequestsByEmployee(String employeeId, int page, int size) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<LeaveRequestEntity> leaveRequestEntities = leaveRequestRepository.findAllByEmployeeId(employeeId, pageable);

        return leaveRequestEntities.map(leaveRequestMapper::mapEntityToResponse);
    }

    @Override
    public LeaveResponseDto getLeaveRequestById(String leaveRequestId) {
        LeaveRequestEntity leaveRequestEntity = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave record not found!"));

        return leaveRequestMapper.mapEntityToResponse(leaveRequestEntity);
    }

    @Override
    @Transactional
    public LeaveResponseDto createLeaveRequest(CreateLeaveRequestDto request) {
        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        if (employee.getStatus().equals(EmployeeStatus.TERMINATED)) {
            throw new BadRequestException("Cannot process leave for a terminated employee!");
        }

        LeaveTypeEntity leaveTypeEntity = leaveTypeRepository.findById(request.leaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found!"));

        if (request.endDate().isBefore(request.startDate())) {
            throw new BadRequestException("End date cannot be before start date!");
        }

        if (leaveRequestRepository.existsOverlappingLeave(request.employeeId(), request.startDate(), request.endDate(),
                LeaveStatus.PENDING, LeaveStatus.APPROVED)) {
            throw new ConflictException("Employee already has a pending or approved leave in this date range!");
        }

        int requestedDays = (int) ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;
        int currentYear = request.startDate().getYear();
        int usedDays = leaveRequestRepository.countUsedDaysByEmployeeAndLeaveTypeInYear(request.employeeId(),
                leaveTypeEntity.getId(), LeaveStatus.APPROVED, currentYear);

        if (usedDays + requestedDays > leaveTypeEntity.getMaxDaysPerYear()) {
            int remainingDays = Math.max(0, leaveTypeEntity.getMaxDaysPerYear() - usedDays);
            throw new BadRequestException("Leave limit exceeded! Allowed max days for " + leaveTypeEntity.getName() +
                            " is " + leaveTypeEntity.getMaxDaysPerYear() + ". You have " + remainingDays + " days remaining.");
        }

        LeaveRequestEntity leaveRequestEntity = leaveRequestMapper.mapCreateRequestDtoToEntity(request);
        leaveRequestEntity.setEmployee(employee);
        leaveRequestEntity.setLeaveType(leaveTypeEntity);
        leaveRequestEntity.setTotalDays(requestedDays);
        leaveRequestEntity.setStatus(LeaveStatus.PENDING);
        leaveRequestEntity.setCreatedAt(Instant.now());

        LeaveRequestEntity savedLeaveEntity = leaveRequestRepository.save(leaveRequestEntity);

        return leaveRequestMapper.mapEntityToResponse(savedLeaveEntity);
    }

    @Override
    @Transactional
    public LeaveResponseDto approveLeaveRequest(String leaveRequestId) {
        LeaveRequestEntity leaveRequestEntity = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found!"));

        if (!leaveRequestEntity.getStatus().equals(LeaveStatus.PENDING)) {
            throw new BadRequestException("Leave request is not in PENDING status! Current status: "
                    + leaveRequestEntity.getStatus());
        }

        leaveRequestEntity.setStatus(LeaveStatus.APPROVED);
        leaveRequestEntity.setUpdatedAt(Instant.now());

        LeaveRequestEntity savedLeaveEntity = leaveRequestRepository.save(leaveRequestEntity);

        return leaveRequestMapper.mapEntityToResponse(savedLeaveEntity);
    }

    @Override
    @Transactional
    public LeaveResponseDto rejectLeaveRequest(String leaveRequestId) {
        LeaveRequestEntity leaveRequestEntity = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found!"));

        if (!leaveRequestEntity.getStatus().equals(LeaveStatus.PENDING)) {
            throw new BadRequestException("Leave request is not in PENDING status! Current status: "
                    + leaveRequestEntity.getStatus());
        }

        leaveRequestEntity.setStatus(LeaveStatus.REJECTED);
        leaveRequestEntity.setUpdatedAt(Instant.now());

        LeaveRequestEntity savedLeaveEntity = leaveRequestRepository.save(leaveRequestEntity);

        return leaveRequestMapper.mapEntityToResponse(savedLeaveEntity);
    }

    @Override
    @Transactional
    public LeaveResponseDto cancelLeaveRequest(String leaveRequestId) {
        LeaveRequestEntity leaveRequestEntity = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found!"));

        if (!leaveRequestEntity.getStatus().equals(LeaveStatus.PENDING)) {
            throw new BadRequestException("Leave request is not in PENDING status! Current status: "
                    + leaveRequestEntity.getStatus());
        }

        leaveRequestEntity.setStatus(LeaveStatus.CANCELLED);
        leaveRequestEntity.setUpdatedAt(Instant.now());

        LeaveRequestEntity savedLeaveEntity = leaveRequestRepository.save(leaveRequestEntity);

        return leaveRequestMapper.mapEntityToResponse(savedLeaveEntity);
    }
}
