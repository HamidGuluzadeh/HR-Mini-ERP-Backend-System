package com.hr.erp.payroll.mapper;

import com.hr.erp.employee.mapper.EmployeeNameMapper;
import com.hr.erp.payroll.dto.response.PayrollResponseDto;
import com.hr.erp.payroll.entity.Payroll;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = EmployeeNameMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PayrollMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee", target = "employeeFullName", qualifiedByName = "employeeFullName")
    @Mapping(source = "employee.code", target = "employeeCode")
    @Mapping(source = "employee.position.title", target = "positionName")
    @Mapping(source = "employee.position.department.name", target = "departmentName")
    PayrollResponseDto mapEntityToResponse(Payroll payroll);

}
