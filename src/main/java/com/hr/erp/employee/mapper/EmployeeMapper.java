package com.hr.erp.employee.mapper;

import com.hr.erp.employee.dto.request.EmployeeRequest;
import com.hr.erp.employee.dto.response.EmployeeResponse;
import com.hr.erp.employee.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {

    EmployeeResponse mapEntityToResponse(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Employee mapRequestToEntity(EmployeeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "manager", ignore = true)
    void updateEntityFromRequest(EmployeeRequest request, @MappingTarget Employee employee);

}
