package com.hr.erp.hierarchy.controller;


import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.hierarchy.dto.request.PositionRequest;
import com.hr.erp.hierarchy.dto.response.PositionResponse;
import com.hr.erp.hierarchy.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.hr.erp.common.enums.SuccessStatus.SUCCESS;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
@Tag(name = "Position Controller", description = "Endpoints for managing job positions and organizational titles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PositionController {
    PositionService positionService;

    @GetMapping("/all")
    @Operation(summary = "Get all positions", description = "Retrieves a paginated list of all job positions")
    public ResponseEntity<SuccessDto<Page<PositionResponse>>> getAllPositions(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size) {
        Page<PositionResponse> response = positionService.getAllPositions(page, size);
        SuccessDto<Page<PositionResponse>> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/department/{departmentId}/all")
    @Operation(summary = "Get positions by department", description = "Retrieves a paginated list of job positions assigned to a specific department")
    public ResponseEntity<SuccessDto<Page<PositionResponse>>> getPositionsByDepartment(@PathVariable String departmentId,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "10") int size) {
        Page<PositionResponse> response = positionService.getPositionsByDepartment(departmentId, page, size);
        SuccessDto<Page<PositionResponse>> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{positionId}")
    @Operation(summary = "Get position by ID", description = "Retrieves detailed information of a specific position")
    public ResponseEntity<SuccessDto<PositionResponse>> getPositionById(@PathVariable String positionId) {
        PositionResponse response = positionService.getPositionById(positionId);
        SuccessDto<PositionResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    @Operation(summary = "Create position", description = "Creates a new job position within a department")
    public ResponseEntity<SuccessDto<PositionResponse>> createPosition(@RequestBody PositionRequest request) {
        PositionResponse response = positionService.createPosition(request);
        SuccessDto<PositionResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{positionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    @Operation(summary = "Update position", description = "Updates details of an existing job position")
    public ResponseEntity<SuccessDto<PositionResponse>> updatePosition(@PathVariable String positionId,
                                                                       @RequestBody PositionRequest request) {
        PositionResponse response = positionService.updatePosition(positionId, request);
        SuccessDto<PositionResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @DeleteMapping("/{positionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete position", description = "Deletes a job position record from the system")
    public ResponseEntity<Void> deletePosition(@PathVariable String positionId) {
        positionService.deletePosition(positionId);
        return ResponseEntity.noContent().build();
    }
}
