package com.shms.service;

import java.util.List;

import com.shms.dto.request.DepartmentRequest;
import com.shms.dto.response.DepartmentResponse;
import com.shms.dto.response.PageResponse;

public interface DepartmentService {

    DepartmentResponse createDepartment(
            DepartmentRequest request
    );

    DepartmentResponse getDepartmentById(Long id);

    List<DepartmentResponse> getAllDepartments();

    DepartmentResponse updateDepartment(
            Long id,
            DepartmentRequest request
    );

    void deleteDepartment(Long id);

    // =====================================================
    // PAGINATION + SORTING
    // =====================================================

    PageResponse<DepartmentResponse> getAllDepartments(
            int page,
            int size,
            String sortBy,
            String direction
    );

    // =====================================================
    // SEARCH + FILTERING
    // =====================================================

    PageResponse<DepartmentResponse> searchDepartments(
            String keyword,
            Boolean active,
            int page,
            int size,
            String sortBy,
            String direction
    );
}