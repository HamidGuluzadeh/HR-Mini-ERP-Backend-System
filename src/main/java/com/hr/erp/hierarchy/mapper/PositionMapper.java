package com.hr.erp.hierarchy.mapper;

import com.hr.erp.hierarchy.dto.request.PositionRequest;
import com.hr.erp.hierarchy.dto.response.PositionResponse;
import com.hr.erp.hierarchy.entity.Position;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionMapper {

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    PositionResponse mapEntityToResponse(Position position);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    Position mapRequestToEntity(PositionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateEntityFromRequest(PositionRequest request, @MappingTarget Position position);

}
