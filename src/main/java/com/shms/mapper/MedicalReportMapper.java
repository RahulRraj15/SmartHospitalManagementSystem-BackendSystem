package com.shms.mapper;

import com.shms.dto.request.MedicalReportRequest;
import com.shms.dto.response.MedicalReportResponse;
import com.shms.entity.Doctor;
import com.shms.entity.MedicalReport;
import com.shms.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class MedicalReportMapper {

    public MedicalReport toEntity(
            MedicalReportRequest request,
            Patient patient,
            Doctor doctor) {

        return MedicalReport.builder()
                .reportDate(request.getReportDate())
                .diagnosis(request.getDiagnosis())
                .symptoms(request.getSymptoms())
                .treatment(request.getTreatment())
                .observations(request.getObservations())
                .reportFileUrl(request.getReportFileUrl())
                .patient(patient)
                .doctor(doctor)
                .build();
    }

    public void updateEntity(
            MedicalReport report,
            MedicalReportRequest request) {

        report.setReportDate(request.getReportDate());
        report.setDiagnosis(request.getDiagnosis());
        report.setSymptoms(request.getSymptoms());
        report.setTreatment(request.getTreatment());
        report.setObservations(request.getObservations());
        report.setReportFileUrl(request.getReportFileUrl());
    }

    public MedicalReportResponse toResponse(MedicalReport report) {

        String patientName =
                report.getPatient().getUser().getFirstName()
                + " "
                + report.getPatient().getUser().getLastName();

        String doctorName =
                report.getDoctor().getSpecialization();

        return new MedicalReportResponse(
                report.getId(),
                report.getCreatedAt(),
                report.getUpdatedAt(),
                report.getReportDate(),
                report.getDiagnosis(),
                report.getSymptoms(),
                report.getTreatment(),
                report.getObservations(),
                report.getReportFileUrl(),
                report.getPatient().getId(),
                patientName,
                report.getDoctor().getId(),
                doctorName,
                report.getDoctor().getSpecialization()
        );
    }
}