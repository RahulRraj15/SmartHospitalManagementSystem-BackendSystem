package com.shms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalReportRequest {

    @NotNull(message = "Report date is required")
    @PastOrPresent(message = "Report date cannot be in the future")
    private LocalDate reportDate;

    @NotBlank(message = "Diagnosis is required")
    @Size(max = 1000, message = "Diagnosis cannot exceed 1000 characters")
    private String diagnosis;

    @Size(max = 2000, message = "Symptoms cannot exceed 2000 characters")
    private String symptoms;

    @Size(max = 2000, message = "Treatment cannot exceed 2000 characters")
    private String treatment;

    @Size(max = 2000, message = "Observations cannot exceed 2000 characters")
    private String observations;

    @Size(max = 500, message = "Report file URL cannot exceed 500 characters")
    private String reportFileUrl;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
}