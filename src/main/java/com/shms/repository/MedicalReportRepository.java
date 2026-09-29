package com.shms.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shms.entity.MedicalReport;

public interface MedicalReportRepository
        extends JpaRepository<MedicalReport, Long> {

    List<MedicalReport> findByPatientId(Long patientId);

    List<MedicalReport> findByDoctorId(Long doctorId);

    @Query("""
            SELECT mr
            FROM MedicalReport mr
            WHERE
                (
                    :keyword IS NULL
                    OR LOWER(mr.diagnosis)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(mr.symptoms)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(mr.treatment)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(mr.observations)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(mr.reportFileUrl)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))

                    OR LOWER(mr.patient.address)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(mr.patient.emergencyContactName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(mr.patient.emergencyContactPhone)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(mr.patient.user.firstName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(mr.patient.user.lastName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(mr.patient.user.email)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(mr.patient.user.phone)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))

                    OR LOWER(mr.doctor.specialization)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(mr.doctor.qualification)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (
                    :reportDate IS NULL
                    OR mr.reportDate = :reportDate
                )
            AND
                (
                    :patientId IS NULL
                    OR mr.patient.id = :patientId
                )
            AND
                (
                    :doctorId IS NULL
                    OR mr.doctor.id = :doctorId
                )
            """)
    Page<MedicalReport> searchMedicalReports(
            @Param("keyword") String keyword,
            @Param("reportDate") LocalDate reportDate,
            @Param("patientId") Long patientId,
            @Param("doctorId") Long doctorId,
            Pageable pageable
    );
}