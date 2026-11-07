package com.hr.erp.auth.mapper;

import com.hr.erp.auth.dto.request.CreateUserRequestDto;
import com.hr.erp.auth.dto.response.UserResponseDto;
import com.hr.erp.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserResponseDto mapEntityToResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User mapCreateRequestToEntity(CreateUserRequestDto dto);

}
