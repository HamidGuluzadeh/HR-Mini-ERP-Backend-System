package com.hr.erp.hierarchy.controller;


import com.hr.erp.common.dto.SuccessDto;
import com.hr.erp.hierarchy.dto.request.PositionRequest;
import com.hr.erp.hierarchy.dto.response.PositionResponse;
import com.hr.erp.hierarchy.service.PositionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hr.erp.common.enums.SuccessStatus.SUCCESS;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PositionController {
    PositionService positionService;

    @GetMapping("/all")
    public ResponseEntity<SuccessDto<Page<PositionResponse>>> getAllPositions(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size) {
        Page<PositionResponse> response = positionService.getAllPositions(page, size);
        SuccessDto<Page<PositionResponse>> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/department/{departmentId}/all")
    public ResponseEntity<SuccessDto<Page<PositionResponse>>> getPositionsByDepartment(@PathVariable String departmentId,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "10") int size) {
        Page<PositionResponse> response = positionService.getPositionsByDepartment(departmentId, page, size);
        SuccessDto<Page<PositionResponse>> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{positionId}")
    public ResponseEntity<SuccessDto<PositionResponse>> getPositionById(@PathVariable String positionId) {
        PositionResponse response = positionService.getPositionById(positionId);
        SuccessDto<PositionResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<SuccessDto<PositionResponse>> createPosition(@RequestBody PositionRequest request) {
        PositionResponse response = positionService.createPosition(request);
        SuccessDto<PositionResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{positionId}")
    public ResponseEntity<SuccessDto<PositionResponse>> updatePosition(@PathVariable String positionId,
                                                                       @RequestBody PositionRequest request) {
        PositionResponse response = positionService.updatePosition(positionId, request);
        SuccessDto<PositionResponse> successDto = new SuccessDto<>(SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @DeleteMapping("/{positionId}")
    public ResponseEntity<Void> deletePosition(@PathVariable String positionId) {
        positionService.deletePosition(positionId);
        return ResponseEntity.noContent().build();
    }
}
