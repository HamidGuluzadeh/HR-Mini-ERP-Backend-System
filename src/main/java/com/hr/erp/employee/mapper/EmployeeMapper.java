package com.hr.erp.employee.mapper;

import com.hr.erp.employee.dto.request.EmployeeRequest;
import com.hr.erp.employee.dto.response.EmployeeResponse;
import com.hr.erp.employee.entity.Employee;
import com.hr.erp.employee.entity.EmployeeContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = EmployeeNameMapper.class)
public interface EmployeeMapper {

    @Mapping(target = "id", source = "employee.id")
    @Mapping(target = "positionId", source = "employee.position.id")
    @Mapping(target = "positionTitle", source = "employee.position.title")
    @Mapping(target = "departmentName", source = "employee.position.department.name")
    @Mapping(target = "managerId", source = "employee.manager.id")
    @Mapping(target = "managerFullName", source = "employee.manager", qualifiedByName = "employeeFullName")
    @Mapping(target = "salaryType", source = "contract.salaryType")
    @Mapping(target = "baseSalary", source = "contract.baseSalary")
    @Mapping(target = "hourlyRate", source = "contract.hourlyRate")
    @Mapping(target = "createdAt", source = "employee.createdAt")
    @Mapping(target = "createdBy", source = "employee.createdBy")
    @Mapping(target = "updatedAt", source = "employee.updatedAt")
    @Mapping(target = "updatedBy", source = "employee.updatedBy")
    EmployeeResponse mapEntityToResponse(Employee employee, EmployeeContract contract);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Employee mapRequestToEntity(EmployeeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(EmployeeRequest request, @MappingTarget Employee employee);

}
