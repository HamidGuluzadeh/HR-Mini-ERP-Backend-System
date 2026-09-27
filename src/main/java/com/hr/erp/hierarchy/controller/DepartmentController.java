package com.hr.erp.hierarchy.controller;

import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.hierarchy.dto.DepartmentRequest;
import com.hr.erp.hierarchy.dto.DepartmentResponse;
import com.hr.erp.hierarchy.service.DepartmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hr.erp.common.enums.SuccessStatus.SUCCESS;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentController {
    DepartmentService departmentService;

    @GetMapping("/all")
    public ResponseEntity<SuccessDto<Page<DepartmentResponse>>> getAllDepartments(@RequestParam(defaultValue = "0")
                                                                                  int page,
                                                                                  @RequestParam(defaultValue = "10")
                                                                                  int size) {
        Page<DepartmentResponse> response = departmentService.getAllDepartments(page, size);
        SuccessDto<Page<DepartmentResponse>> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<SuccessDto<DepartmentResponse>> getDepartmentById(@PathVariable String departmentId) {
        DepartmentResponse response = departmentService.getDepartmentById(departmentId);
        SuccessDto<DepartmentResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<SuccessDto<DepartmentResponse>> createDepartment(@RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.createDepartment(request);
        SuccessDto<DepartmentResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<SuccessDto<DepartmentResponse>> updateDepartment(@PathVariable String departmentId,
                                                                           @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.updateDepartment(departmentId, request);
        SuccessDto<DepartmentResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String departmentId) {
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.noContent().build();
    }
}
