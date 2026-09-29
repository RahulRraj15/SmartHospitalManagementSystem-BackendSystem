package com.shms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalReportResponse {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDate reportDate;

    private String diagnosis;

    private String symptoms;

    private String treatment;

    private String observations;

    private String reportFileUrl;

    private Long patientId;

    private String patientName;

    private Long doctorId;

    private String doctorName;

    private String doctorSpecialization;
}