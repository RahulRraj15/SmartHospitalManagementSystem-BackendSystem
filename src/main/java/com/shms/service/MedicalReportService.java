package com.shms.service;

import java.time.LocalDate;
import java.util.List;

import com.shms.dto.request.MedicalReportRequest;
import com.shms.dto.response.MedicalReportResponse;
import com.shms.dto.response.PageResponse;

public interface MedicalReportService {

    MedicalReportResponse createMedicalReport(
            MedicalReportRequest request);

    MedicalReportResponse getMedicalReportById(
            Long id);

    List<MedicalReportResponse> getAllMedicalReports();

    List<MedicalReportResponse> getMedicalReportsByPatient(
            Long patientId);

    List<MedicalReportResponse> getMedicalReportsByDoctor(
            Long doctorId);

    MedicalReportResponse updateMedicalReport(
            Long id,
            MedicalReportRequest request);

    void deleteMedicalReport(Long id);

    PageResponse<MedicalReportResponse> getAllMedicalReports(
            int page,
            int size,
            String sortBy,
            String direction
    );

    PageResponse<MedicalReportResponse> searchMedicalReports(
            String keyword,
            LocalDate reportDate,
            Long patientId,
            Long doctorId,
            int page,
            int size,
            String sortBy,
            String direction
    );
}