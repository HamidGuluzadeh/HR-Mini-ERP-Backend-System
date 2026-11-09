package com.hr.erp.employee.mapper;

import com.hr.erp.employee.dto.request.EmployeeRequest;
import com.hr.erp.employee.dto.response.EmployeeResponse;
import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.entity.EmployeeContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {
    @Mapping(target = "id", source = "employee.id")
    @Mapping(target = "salaryType", source = "contract.salaryType")
    @Mapping(target = "baseSalary", source = "contract.baseSalary")
    @Mapping(target = "hourlyRate", source = "contract.hourlyRate")
    EmployeeResponse mapEntityToResponse(Employee employee, EmployeeContract contract);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Employee mapRequestToEntity(EmployeeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "manager", ignore = true)
    void updateEntityFromRequest(EmployeeRequest request, @MappingTarget Employee employee);

}
