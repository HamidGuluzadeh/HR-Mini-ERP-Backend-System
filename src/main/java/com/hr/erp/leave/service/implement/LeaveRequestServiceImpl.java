package com.hr.erp.leave.service.implement;

import com.hr.erp.auth.entity.User;
import com.hr.erp.auth.repository.UserRepository;
import com.hr.erp.common.exception.BadRequestException;
import com.hr.erp.common.exception.ConflictException;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.mapper.EmployeeNameMapper;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeaveRequestServiceImpl implements LeaveRequestService {
    LeaveRequestRepository leaveRequestRepository;
    LeaveTypeRepository leaveTypeRepository;
    EmployeeRepository employeeRepository;
    LeaveRequestMapper leaveRequestMapper;
    UserRepository userRepository;
    EmployeeNameMapper employeeNameMapper;

    static ZoneId COMPANY_ZONE_ID = ZoneId.of("Asia/Baku");

    @Override
    public Page<LeaveResponseDto> getLeaveRequestsByStatus(LeaveStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<LeaveRequestEntity> leaveRequestEntities = leaveRequestRepository.findAllByStatus(status, pageable);

        return toResponsePage(leaveRequestEntities);
    }

    @Override
    public Page<LeaveResponseDto> getLeaveRequestsByEmployee(String employeeId, int page, int size) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<LeaveRequestEntity> leaveRequestEntities = leaveRequestRepository.findAllByEmployeeId(employeeId, pageable);

        return toResponsePage(leaveRequestEntities);
    }

    @Override
    public LeaveResponseDto getLeaveRequestById(String leaveRequestId) {
        LeaveRequestEntity leaveRequestEntity = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave record not found!"));

        return toResponse(leaveRequestEntity);
    }

    @Override
    @Transactional
    public LeaveResponseDto createLeaveRequest(CreateLeaveRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "SYSTEM";

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
        leaveRequestEntity.setCreatedBy(userId);

        LeaveRequestEntity savedLeaveEntity = leaveRequestRepository.save(leaveRequestEntity);

        return toResponse(savedLeaveEntity);
    }

    @Override
    @Transactional
    public LeaveResponseDto approveLeaveRequest(String leaveRequestId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "SYSTEM";

        LeaveRequestEntity leaveRequestEntity = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found!"));

        if (!leaveRequestEntity.getStatus().equals(LeaveStatus.PENDING)) {
            throw new BadRequestException("Leave request is not in PENDING status! Current status: "
                    + leaveRequestEntity.getStatus());
        }

        leaveRequestEntity.setStatus(LeaveStatus.APPROVED);
        leaveRequestEntity.setApprovedBy(userId);
        leaveRequestEntity.setUpdatedBy(userId);
        leaveRequestEntity.setUpdatedAt(Instant.now());

        LeaveRequestEntity savedLeaveEntity = leaveRequestRepository.save(leaveRequestEntity);

        return toResponse(savedLeaveEntity);
    }

    @Override
    @Transactional
    public LeaveResponseDto rejectLeaveRequest(String leaveRequestId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "SYSTEM";

        LeaveRequestEntity leaveRequestEntity = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found!"));

        if (!leaveRequestEntity.getStatus().equals(LeaveStatus.PENDING)) {
            throw new BadRequestException("Leave request is not in PENDING status! Current status: "
                    + leaveRequestEntity.getStatus());
        }

        leaveRequestEntity.setStatus(LeaveStatus.REJECTED);
        leaveRequestEntity.setUpdatedAt(Instant.now());
        leaveRequestEntity.setUpdatedBy(userId);

        LeaveRequestEntity savedLeaveEntity = leaveRequestRepository.save(leaveRequestEntity);

        return toResponse(savedLeaveEntity);
    }

    @Override
    @Transactional
    public LeaveResponseDto cancelLeaveRequest(String leaveRequestId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "SYSTEM";

        LeaveRequestEntity leaveRequestEntity = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found!"));

        if (!leaveRequestEntity.getStatus().equals(LeaveStatus.PENDING)) {
            throw new BadRequestException("Leave request is not in PENDING status! Current status: "
                    + leaveRequestEntity.getStatus());
        }

        leaveRequestEntity.setStatus(LeaveStatus.CANCELLED);
        leaveRequestEntity.setUpdatedAt(Instant.now());
        leaveRequestEntity.setUpdatedBy(userId);

        LeaveRequestEntity savedLeaveEntity = leaveRequestRepository.save(leaveRequestEntity);

        return toResponse(savedLeaveEntity);
    }

    private LeaveResponseDto toResponse(LeaveRequestEntity leaveRequestEntity) {
        LeaveResponseDto dto = leaveRequestMapper.mapEntityToResponse(leaveRequestEntity);

        if (Objects.isNull(dto.approvedById())) {
            return dto;
        }

        return withApproverName(dto, resolveApproverNames(Set.of(dto.approvedById())));
    }

    // Resolves all approver names of a page with a single query (avoids N+1)
    private Page<LeaveResponseDto> toResponsePage(Page<LeaveRequestEntity> leaveRequestEntities) {
        Set<String> approverIds = leaveRequestEntities.getContent().stream()
                .map(LeaveRequestEntity::getApprovedBy)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, String> approverNames = resolveApproverNames(approverIds);

        return leaveRequestEntities.map(entity ->
                withApproverName(leaveRequestMapper.mapEntityToResponse(entity), approverNames));
    }

    private Map<String, String> resolveApproverNames(Set<String> approverIds) {
        if (approverIds.isEmpty()) {
            return Map.of();
        }

        return userRepository.findAllByIdIn(approverIds).stream()
                .collect(Collectors.toMap(User::getId, user -> employeeNameMapper.toFullName(user.getEmployee())));
    }

    private LeaveResponseDto withApproverName(LeaveResponseDto dto, Map<String, String> approverNames) {
        if (Objects.isNull(dto.approvedById())) {
            return dto;
        }

        return new LeaveResponseDto(dto.id(), dto.employeeId(), dto.employeeFullName(), dto.leaveTypeId(),
                dto.leaveTypeName(), dto.startDate(), dto.endDate(), dto.totalDays(), dto.status(),
                dto.approvedById(), approverNames.get(dto.approvedById()), dto.reason(),
                dto.createdAt(), dto.createdBy(), dto.updatedAt(), dto.updatedBy());
    }
}
