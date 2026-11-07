package com.hr.erp.auth.service;

import com.hr.erp.auth.dto.request.ChangePasswordRequestDto;
import com.hr.erp.auth.dto.request.CreateUserRequestDto;
import com.hr.erp.auth.dto.response.UserResponseDto;
import com.hr.erp.auth.model.UserStatus;
import org.springframework.data.domain.Page;

public interface UserService {

    Page<UserResponseDto> getUsersByDepartmentId(String departmentId, int page, int size);

    Page<UserResponseDto> getUsersByDepartmentCode(String departmentCode, int page, int size);

    UserResponseDto getUserById(String userId);

    UserResponseDto createUser(CreateUserRequestDto request);

    void changePassword(ChangePasswordRequestDto request);

    void changeStatus(String userId, UserStatus status);

}
