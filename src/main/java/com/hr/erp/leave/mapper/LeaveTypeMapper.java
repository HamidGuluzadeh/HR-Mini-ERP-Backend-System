package com.hr.erp.leave.mapper;

import com.hr.erp.leave.dto.request.CreateLeaveTypeRequestDto;
import com.hr.erp.leave.dto.request.UpdateLeaveTypeRequestDto;
import com.hr.erp.leave.dto.response.LeaveTypeResponseDto;
import com.hr.erp.leave.entity.LeaveTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LeaveTypeMapper {

    LeaveTypeResponseDto mapEntityToResponse(LeaveTypeEntity leaveTypeEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    LeaveTypeEntity mapRequestToEntity(CreateLeaveTypeRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateLeaveTypeRequestDto requestDto, @MappingTarget LeaveTypeEntity leaveTypeEntity);

}
