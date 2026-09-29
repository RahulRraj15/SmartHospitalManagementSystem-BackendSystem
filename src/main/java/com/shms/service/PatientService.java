package com.shms.service;

import java.util.List;

import com.shms.dto.request.PatientRequest;
import com.shms.dto.response.PageResponse;
import com.shms.dto.response.PatientResponse;
import com.shms.entity.enums.BloodGroup;
import com.shms.entity.enums.Gender;

public interface PatientService {

    PatientResponse createPatient(PatientRequest request);

    PatientResponse getPatientById(Long id);

    List<PatientResponse> getAllPatients();

    PageResponse<PatientResponse> getAllPatients(
            int page,
            int size,
            String sortBy,
            String direction
    );

    PageResponse<PatientResponse> searchPatients(
            String keyword,
            Gender gender,
            BloodGroup bloodGroup,
            int page,
            int size,
            String sortBy,
            String direction
    );

    PatientResponse updatePatient(
            Long id,
            PatientRequest request
    );

    void deletePatient(Long id);
}