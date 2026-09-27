package com.hr.erp.hierarchy.mapper;

import com.hr.erp.hierarchy.dto.DepartmentRequest;
import com.hr.erp.hierarchy.dto.DepartmentResponse;
import com.hr.erp.hierarchy.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentMapper {

    @Mapping(target = "parentId", source = "parent.id")
    DepartmentResponse mapEntityToResponse(Department department);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Department mapRequestToEntity(DepartmentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(DepartmentRequest request, @MappingTarget Department department);

}
