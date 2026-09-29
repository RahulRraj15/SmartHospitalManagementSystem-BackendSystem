package com.shms.service;

import java.time.LocalDate;
import java.util.List;

import com.shms.dto.request.AppointmentRequest;
import com.shms.dto.response.AppointmentResponse;
import com.shms.dto.response.PageResponse;
import com.shms.entity.enums.AppointmentStatus;

public interface AppointmentService {

    AppointmentResponse createAppointment(
            AppointmentRequest request
    );

    AppointmentResponse getAppointmentById(Long id);

    List<AppointmentResponse> getAllAppointments();

    AppointmentResponse updateAppointment(
            Long id,
            AppointmentRequest request
    );

    void deleteAppointment(Long id);

    PageResponse<AppointmentResponse> getAllAppointments(
            int page,
            int size,
            String sortBy,
            String direction
    );

    PageResponse<AppointmentResponse> searchAppointments(
            String keyword,
            AppointmentStatus status,
            LocalDate appointmentDate,
            int page,
            int size,
            String sortBy,
            String direction
    );
}