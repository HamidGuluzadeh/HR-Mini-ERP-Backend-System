package com.hr.erp.attendance.service.implement;

import com.hr.erp.attendance.dto.request.AttendanceRequest;
import com.hr.erp.attendance.dto.response.AttendanceResponse;
import com.hr.erp.attendance.entity.Attendance;
import com.hr.erp.attendance.mapper.AttendanceMapper;
import com.hr.erp.attendance.repository.AttendanceRepository;
import com.hr.erp.attendance.service.AttendanceService;
import com.hr.erp.common.enums.AttendanceStatus;
import com.hr.erp.common.enums.EmployeeStatus;
import com.hr.erp.common.exception.BadRequestException;
import com.hr.erp.common.exception.ConflictException;
import com.hr.erp.common.exception.ResourceAlreadyExistsException;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceServiceImpl implements AttendanceService {
    AttendanceRepository attendanceRepository;
    AttendanceMapper attendanceMapper;
    EmployeeRepository employeeRepository;

    static LocalTime WORK_START_TIME = LocalTime.of(9, 0);
    static LocalTime LATE_THRESHOLD = LocalTime.of(9, 10);
    static LocalTime AFTERNOON_CHECKIN_THRESHOLD = LocalTime.of(13, 0);
    static LocalTime EARLY_LEAVE_THRESHOLD = LocalTime.of(14, 0);
    static ZoneId COMPANY_ZONE_ID = ZoneId.of("Asia/Baku");

    @Override
    public Page<AttendanceResponse> getAttendancesByEmployee(String employeeId, int page, int size) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Attendance> attendances = attendanceRepository.findAllByEmployeeId(employeeId, pageable);

        return attendances.map(attendanceMapper::mapEntityToResponse);
    }

    @Override
    public Page<AttendanceResponse> getAttendancesByDate(LocalDate workDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Attendance> attendances = attendanceRepository.findAllByWorkDate(workDate, pageable);

        return attendances.map(attendanceMapper::mapEntityToResponse);
    }

    @Override
    public Page<AttendanceResponse> getAttendancesByDateRange(LocalDate startDate, LocalDate endDate, int page, int size) {
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date cannot be before start date!");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Attendance> attendances = attendanceRepository.findAllByWorkDateBetween(startDate, endDate, pageable);

        return attendances.map(attendanceMapper::mapEntityToResponse);
    }

    @Override
    public AttendanceResponse getAttendanceById(String attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found!"));

        return attendanceMapper.mapEntityToResponse(attendance);
    }

    @Override
    @Transactional
    public AttendanceResponse checkIn(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        if (employee.getStatus().equals(EmployeeStatus.TERMINATED)) {
            throw new BadRequestException("Employee has already been terminated!");
        }

        if (attendanceRepository.existsByEmployeeIdAndWorkDate(employeeId, LocalDate.now())) {
            throw new ResourceAlreadyExistsException("Employee has already been checked in today!");
        }

        Instant now = Instant.now();

        AttendanceStatus attendanceStatus = calculateAttendance(now);

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .workDate(LocalDate.now())
                .checkIn(now)
                .status(attendanceStatus)
                .createdAt(now)
                .createdBy("SYSTEM")
                .build();

        Attendance savedAttendance = attendanceRepository.save(attendance);

        return attendanceMapper.mapEntityToResponse(savedAttendance);
    }

    @Override
    @Transactional
    public AttendanceResponse checkOut(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        if (employee.getStatus().equals(EmployeeStatus.TERMINATED)) {
            throw new BadRequestException("Employee has already been terminated!");
        }

        Attendance attendance = attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("No check-in record found for today!"));

        if (!Objects.isNull(attendance.getCheckOut())) {
            throw new ResourceAlreadyExistsException("Employee has already been checked out today!");
        }

        attendance.setCheckOut(Instant.now());

        Attendance savedAttendance = attendanceRepository.save(attendance);

        return attendanceMapper.mapEntityToResponse(savedAttendance);
    }

    @Override
    @Transactional
    public AttendanceResponse createAttendance(AttendanceRequest request) {
        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        if (employee.getStatus().equals(EmployeeStatus.TERMINATED)) {
            throw new BadRequestException("Employee has already been terminated!");
        }

        if (attendanceRepository.existsByEmployeeIdAndWorkDate(request.employeeId(), request.workDate())) {
            throw new ResourceAlreadyExistsException("Attendance record already exists for employee on " + request.workDate());
        }

        if (request.workDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Work date cannot be after today!");
        }

        if (!Objects.isNull(request.checkIn()) && !Objects.isNull(request.checkOut())
                && request.checkOut().isBefore(request.checkIn())) {
            throw new BadRequestException("Check-out time cannot be before check-in time!");
        }

        Attendance attendance = attendanceMapper.mapRequestToEntity(request);

        if (Objects.isNull(request.status())) {
            attendance.setStatus(calculateAttendance(request.checkIn()));
        }

        attendance.setEmployee(employee);
        attendance.setCreatedAt(Instant.now());

        Attendance savedAttendance = attendanceRepository.save(attendance);

        return attendanceMapper.mapEntityToResponse(savedAttendance);
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendance(String attendanceId, AttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found!"));

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        if (employee.getStatus().equals(EmployeeStatus.TERMINATED)) {
            throw new BadRequestException("Employee has already been terminated!");
        }

        if (!attendance.getWorkDate().equals(request.workDate()) &&
                attendanceRepository.existsByEmployeeIdAndWorkDate(request.employeeId(), request.workDate())) {
            throw new ResourceAlreadyExistsException("Attendance record already exists for employee on " + request.workDate());
        }

        if (request.workDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Work date cannot be after today!");
        }

        if (!Objects.isNull(request.checkIn()) && !Objects.isNull(request.checkOut())
                && request.checkOut().isBefore(request.checkIn())) {
            throw new BadRequestException("Check-out time cannot be before check-in time!");
        }

        attendanceMapper.updateEntityFromRequest(request, attendance);
        attendance.setEmployee(employee);
        attendance.setUpdatedAt(Instant.now());

        Attendance savedAttendance = attendanceRepository.save(attendance);

        return attendanceMapper.mapEntityToResponse(savedAttendance);
    }

    private AttendanceStatus calculateAttendance(Instant checkIn) {
        if (Objects.isNull(checkIn)) {
            return AttendanceStatus.ABSENT;
        }

        LocalTime checkInTime = checkIn.atZone(COMPANY_ZONE_ID).toLocalTime();

        if (checkInTime.isAfter(AFTERNOON_CHECKIN_THRESHOLD)) {
            return AttendanceStatus.HALF_DAY;
        }

        if (checkInTime.isAfter(LATE_THRESHOLD)) {
            return AttendanceStatus.LATE;
        }

        return AttendanceStatus.PRESENT;
    }
}
