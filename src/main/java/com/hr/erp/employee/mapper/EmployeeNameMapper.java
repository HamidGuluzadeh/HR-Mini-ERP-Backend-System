package com.hr.erp.employee.mapper;

import com.hr.erp.employee.entity.Employee;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class EmployeeNameMapper {

    @Named("employeeFullName")
    public String toFullName(Employee employee) {
        if (employee == null) {
            return null;
        }

        return employee.getFirstName() + " " + employee.getLastName();
    }
}
