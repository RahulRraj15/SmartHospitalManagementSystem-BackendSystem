package com.shms.mapper;

import com.shms.dto.request.DepartmentRequest;
import com.shms.dto.response.DepartmentResponse;
import com.shms.entity.Department;

public class DepartmentMapper {

    private DepartmentMapper() {
    }

    public static Department toEntity(DepartmentRequest request) {

        return Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .build();
    }

    public static DepartmentResponse toResponse(Department department) {

        return DepartmentResponse.builder()
                .id(department.getId())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .name(department.getName())
                .description(department.getDescription())
                .active(department.isActive())
                .build();
    }
}