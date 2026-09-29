package com.shms.mapper;

import com.shms.dto.request.AppointmentRequest;
import com.shms.dto.response.AppointmentResponse;
import com.shms.entity.Appointment;
import com.shms.entity.Doctor;
import com.shms.entity.Patient;

public class AppointmentMapper {

    private AppointmentMapper() {
    }

    public static Appointment toEntity(
            AppointmentRequest request,
            Doctor doctor,
            Patient patient) {

        return Appointment.builder()
                .appointmentDate(
                        request.getAppointmentDate()
                )
                .appointmentTime(
                        request.getAppointmentTime()
                )
                .reason(request.getReason())
                .notes(request.getNotes())
                .status(
                        com.shms.entity.enums.AppointmentStatus
                                .SCHEDULED
                )
                .doctor(doctor)
                .patient(patient)
                .build();
    }

    public static AppointmentResponse toResponse(
            Appointment appointment) {

        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .appointmentDate(
                        appointment.getAppointmentDate()
                )
                .appointmentTime(
                        appointment.getAppointmentTime()
                )
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .status(appointment.getStatus())
                .doctorId(doctor.getId())
                .doctorSpecialization(
                        doctor.getSpecialization()
                )
                .patientId(patient.getId())
                .patientUserId(
                        patient.getUser().getId()
                )
                .patientFirstName(
                        patient.getUser().getFirstName()
                )
                .patientLastName(
                        patient.getUser().getLastName()
                )
                .build();
    }
}