package com.hr.erp.hierarchy.service;

import com.hr.erp.hierarchy.dto.request.PositionRequest;
import com.hr.erp.hierarchy.dto.response.PositionResponse;
import org.springframework.data.domain.Page;

public interface PositionService {

    Page<PositionResponse> getAllPositions(int page, int size);

    Page<PositionResponse> getPositionsByDepartment(String departmentId, int page, int size);

    PositionResponse getPositionById(String positionId);

    PositionResponse createPosition(PositionRequest request);

    PositionResponse updatePosition(String positionId, PositionRequest request);

    void deletePosition(String positionId);

}
