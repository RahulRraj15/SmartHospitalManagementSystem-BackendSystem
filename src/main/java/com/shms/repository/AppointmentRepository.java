package com.shms.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shms.entity.Appointment;
import com.shms.entity.enums.AppointmentStatus;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    );

    boolean existsByPatientIdAndAppointmentDateAndAppointmentTime(
            Long patientId,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    );

    @Query("""
            SELECT a
            FROM Appointment a
            WHERE
                (
                    :keyword IS NULL
                    OR LOWER(a.reason)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.notes)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.doctor.specialization)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.doctor.qualification)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.patient.address)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.patient.user.firstName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.patient.user.lastName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.patient.user.email)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.patient.user.phone)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (:status IS NULL OR a.status = :status)
            AND
                (:appointmentDate IS NULL
                    OR a.appointmentDate = :appointmentDate)
            """)
    Page<Appointment> searchAppointments(
            @Param("keyword") String keyword,
            @Param("status") AppointmentStatus status,
            @Param("appointmentDate") LocalDate appointmentDate,
            Pageable pageable
    );
}