package com.shms.mapper;

import com.shms.dto.response.PrescriptionMedicineResponse;
import com.shms.dto.response.PrescriptionResponse;
import com.shms.entity.Prescription;
import com.shms.entity.PrescriptionMedicine;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PrescriptionMapper {

    public PrescriptionResponse toResponse(
            Prescription prescription) {

        List<PrescriptionMedicineResponse> medicines =
                prescription.getMedicines()
                        .stream()
                        .map(this::toMedicineResponse)
                        .toList();

        String patientName =
                prescription.getAppointment()
                        .getPatient()
                        .getUser()
                        .getFirstName()
                + " "
                + prescription.getAppointment()
                        .getPatient()
                        .getUser()
                        .getLastName();

        String doctorName =
                prescription.getAppointment()
                        .getDoctor()
                        .getSpecialization();

        return new PrescriptionResponse(
                prescription.getId(),
                prescription.getCreatedAt(),
                prescription.getUpdatedAt(),
                prescription.getPrescriptionDate(),
                prescription.getInstructions(),
                prescription.getAppointment().getId(),
                prescription.getAppointment().getPatient().getId(),
                patientName,
                prescription.getAppointment().getDoctor().getId(),
                doctorName,
                medicines
        );
    }

    private PrescriptionMedicineResponse toMedicineResponse(
            PrescriptionMedicine item) {

        return new PrescriptionMedicineResponse(
                item.getId(),
                item.getMedicine().getId(),
                item.getMedicine().getName(),
                item.getQuantity(),
                item.getDosage(),
                item.getFrequency(),
                item.getDuration(),
                item.getInstructions()
        );
    }
}