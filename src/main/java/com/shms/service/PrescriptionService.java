package com.shms.service;

import java.time.LocalDate;
import java.util.List;

import com.shms.dto.request.PrescriptionRequest;
import com.shms.dto.response.PageResponse;
import com.shms.dto.response.PrescriptionResponse;

public interface PrescriptionService {

    PrescriptionResponse createPrescription(
            PrescriptionRequest request);

    PrescriptionResponse getPrescriptionById(Long id);

    List<PrescriptionResponse> getAllPrescriptions();

    PrescriptionResponse getPrescriptionByAppointment(
            Long appointmentId);

    PrescriptionResponse updatePrescription(
            Long id,
            PrescriptionRequest request);

    void deletePrescription(Long id);

    PageResponse<PrescriptionResponse> getAllPrescriptions(
            int page,
            int size,
            String sortBy,
            String direction
    );

    PageResponse<PrescriptionResponse> searchPrescriptions(
            String keyword,
            LocalDate prescriptionDate,
            Long appointmentId,
            int page,
            int size,
            String sortBy,
            String direction
    );
}