package com.hr.erp.leave.mapper;

import com.hr.erp.employee.mapper.EmployeeNameMapper;
import com.hr.erp.leave.dto.request.CreateLeaveRequestDto;
import com.hr.erp.leave.dto.response.LeaveResponseDto;
import com.hr.erp.leave.entity.LeaveRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = EmployeeNameMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LeaveRequestMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeFullName", source = "employee", qualifiedByName = "employeeFullName")
    @Mapping(target = "leaveTypeId", source = "leaveType.id")
    @Mapping(target = "leaveTypeName", source = "leaveType.name")
    @Mapping(target = "approvedById", source = "approvedBy")
    @Mapping(target = "approvedByFullName", ignore = true)
    LeaveResponseDto mapEntityToResponse(LeaveRequestEntity leaveRequestEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "leaveType", ignore = true)
    @Mapping(target = "totalDays", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    LeaveRequestEntity mapCreateRequestDtoToEntity(CreateLeaveRequestDto createLeaveRequestDto);

}
