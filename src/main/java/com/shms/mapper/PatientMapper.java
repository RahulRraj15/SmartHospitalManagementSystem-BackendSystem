package com.shms.mapper;

import com.shms.dto.request.PatientRequest;
import com.shms.dto.response.PatientResponse;
import com.shms.entity.Patient;
import com.shms.entity.User;

public class PatientMapper {

    private PatientMapper() {
    }

    public static Patient toEntity(
            PatientRequest request,
            User user) {

        return Patient.builder()
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .bloodGroup(request.getBloodGroup())
                .address(request.getAddress())
                .emergencyContactName(
                        request.getEmergencyContactName()
                )
                .emergencyContactPhone(
                        request.getEmergencyContactPhone()
                )
                .user(user)
                .build();
    }

    public static PatientResponse toResponse(
            Patient patient) {

        User user = patient.getUser();

        return PatientResponse.builder()
                .id(patient.getId())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .bloodGroup(patient.getBloodGroup())
                .address(patient.getAddress())
                .emergencyContactName(
                        patient.getEmergencyContactName()
                )
                .emergencyContactPhone(
                        patient.getEmergencyContactPhone()
                )
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}