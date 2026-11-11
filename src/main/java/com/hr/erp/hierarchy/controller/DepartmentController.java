package com.hr.erp.hierarchy.controller;

import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.hierarchy.dto.request.DepartmentRequest;
import com.hr.erp.hierarchy.dto.response.DepartmentResponse;
import com.hr.erp.hierarchy.service.DepartmentService;
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
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Department Controller", description = "Endpoints for managing organizational department hierarchy and structure")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentController {
    DepartmentService departmentService;

    @GetMapping("/all")
    @Operation(summary = "Get all departments", description = "Retrieves a paginated list of all organizational departments")
    public ResponseEntity<SuccessDto<Page<DepartmentResponse>>> getAllDepartments(@RequestParam(defaultValue = "0")
                                                                                  int page,
                                                                                  @RequestParam(defaultValue = "10")
                                                                                  int size) {
        Page<DepartmentResponse> response = departmentService.getAllDepartments(page, size);
        SuccessDto<Page<DepartmentResponse>> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{departmentId}")
    @Operation(summary = "Get department by ID", description = "Retrieves detailed information of a specific department by ID")
    public ResponseEntity<SuccessDto<DepartmentResponse>> getDepartmentById(@PathVariable String departmentId) {
        DepartmentResponse response = departmentService.getDepartmentById(departmentId);
        SuccessDto<DepartmentResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create department", description = "Creates a new department within the organizational structure")
    public ResponseEntity<SuccessDto<DepartmentResponse>> createDepartment(@RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.createDepartment(request);
        SuccessDto<DepartmentResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{departmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update department", description = "Updates details of an existing department")
    public ResponseEntity<SuccessDto<DepartmentResponse>> updateDepartment(@PathVariable String departmentId,
                                                                           @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.updateDepartment(departmentId, request);
        SuccessDto<DepartmentResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @DeleteMapping("/{departmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete department", description = "Removes a department record from the system")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String departmentId) {
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.noContent().build();
    }
}
