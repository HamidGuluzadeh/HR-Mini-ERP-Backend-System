package com.hr.erp.auth.service.implement;

import com.hr.erp.auth.dto.request.ChangePasswordRequestDto;
import com.hr.erp.auth.dto.request.CreateUserRequestDto;
import com.hr.erp.auth.dto.response.UserResponseDto;
import com.hr.erp.auth.entity.User;
import com.hr.erp.auth.mapper.UserMapper;
import com.hr.erp.auth.model.UserStatus;
import com.hr.erp.auth.repository.UserRepository;
import com.hr.erp.auth.service.UserService;
import com.hr.erp.common.exception.BadRequestException;
import com.hr.erp.common.exception.ConflictException;
import com.hr.erp.common.exception.ResourceNotFoundException;
import com.hr.erp.common.exception.UnauthorizedException;
import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.model.EmployeeStatus;
import com.hr.erp.employee.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    EmployeeRepository employeeRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    @Override
    public Page<UserResponseDto> getUsersByDepartmentId(String departmentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> users = userRepository.findAllByEmployee_Position_Department_Id(departmentId, pageable);

        return users.map(userMapper::mapEntityToResponse);
    }

    @Override
    public Page<UserResponseDto> getUsersByDepartmentCode(String departmentCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> users = userRepository.findAllByEmployee_Position_Department_Code(departmentCode, pageable);

        return users.map(userMapper::mapEntityToResponse);
    }

    @Override
    public UserResponseDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        return userMapper.mapEntityToResponse(user);
    }

    @Override
    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto request) {
        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found!"));

        if (employee.getStatus().equals(EmployeeStatus.TERMINATED)) {
            throw new BadRequestException("Cannot create user account for a terminated employee!");
        }

        if (userRepository.existsByEmployeeId(employee.getId())) {
            throw new ConflictException("User account already exists for this employee!");
        }

        User user = userMapper.mapCreateRequestToEntity(request);
        user.setEmployee(employee);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(Instant.now());

        User savedUser = userRepository.save(user);

        return userMapper.mapEntityToResponse(savedUser);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Incorrect old password!");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new BadRequestException("New password cannot be the same as the old password!");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changeStatus(String userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if (user.getStatus().equals(status)) {
            throw new ConflictException("User is already in " + status + " status!");
        }

        user.setStatus(status);
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);
    }
}
