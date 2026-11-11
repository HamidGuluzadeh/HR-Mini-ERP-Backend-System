package com.hr.erp.auth.mapper;

import com.hr.erp.auth.dto.request.CreateUserRequestDto;
import com.hr.erp.auth.dto.response.UserResponseDto;
import com.hr.erp.auth.entity.User;
import com.hr.erp.auth.model.UserStatus;
import com.hr.erp.employee.mapper.EmployeeNameMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", imports = UserStatus.class, uses = EmployeeNameMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeCode", source = "employee.code")
    @Mapping(target = "employeeFullName", source = "employee", qualifiedByName = "employeeFullName")
    @Mapping(target = "isActive", expression = "java(user.getStatus() == UserStatus.ACTIVE)")
    UserResponseDto mapEntityToResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User mapCreateRequestToEntity(CreateUserRequestDto dto);

}
