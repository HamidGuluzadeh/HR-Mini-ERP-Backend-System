package com.hr.erp.attendance.mapper;

import com.hr.erp.attendance.dto.request.AttendanceRequest;
import com.hr.erp.attendance.dto.response.AttendanceResponse;
import com.hr.erp.attendance.entity.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AttendanceMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "firstName", source = "employee.firstName")
    @Mapping(target = "lastName", source = "employee.lastName")
    AttendanceResponse mapEntityToResponse(Attendance attendance);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Attendance mapRequestToEntity(AttendanceRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(AttendanceRequest request, @MappingTarget Attendance attendance);

}
