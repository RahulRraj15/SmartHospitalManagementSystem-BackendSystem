package com.shms.service;

import com.shms.dto.request.DoctorRequest;
import com.shms.dto.response.DoctorResponse;
import com.shms.dto.response.PageResponse;

import java.util.List;

public interface DoctorService {

    DoctorResponse createDoctor(DoctorRequest request);

    DoctorResponse getDoctorById(Long id);

    List<DoctorResponse> getAllDoctors();

    // =====================================================
    // PAGINATION + SORTING
    // =====================================================

    PageResponse<DoctorResponse> getAllDoctors(
            int page,
            int size,
            String sortBy,
            String direction
    );

    // =====================================================
    // SEARCH + FILTERING
    // =====================================================

    PageResponse<DoctorResponse> searchDoctors(
            String keyword,
            Boolean active,
            int page,
            int size,
            String sortBy,
            String direction
    );

    DoctorResponse updateDoctor(
            Long id,
            DoctorRequest request
    );

    void deleteDoctor(Long id);
}