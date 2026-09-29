package com.shms.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shms.entity.Prescription;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByAppointmentId(Long appointmentId);

    boolean existsByAppointmentId(Long appointmentId);

    @Query("""
            SELECT p
            FROM Prescription p
            WHERE
                (
                    :keyword IS NULL
                    OR LOWER(p.instructions)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.appointment.reason)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.appointment.notes)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.appointment.doctor.specialization)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.appointment.doctor.qualification)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.appointment.patient.user.firstName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.appointment.patient.user.lastName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.appointment.patient.user.email)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.appointment.patient.user.phone)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (
                    :prescriptionDate IS NULL
                    OR p.prescriptionDate = :prescriptionDate
                )
            AND
                (
                    :appointmentId IS NULL
                    OR p.appointment.id = :appointmentId
                )
            """)
    Page<Prescription> searchPrescriptions(
            @Param("keyword") String keyword,
            @Param("prescriptionDate") LocalDate prescriptionDate,
            @Param("appointmentId") Long appointmentId,
            Pageable pageable
    );
}