package com.shms.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shms.entity.Patient;
import com.shms.entity.enums.BloodGroup;
import com.shms.entity.enums.Gender;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("""
            SELECT p
            FROM Patient p
            WHERE
                (
                    :keyword IS NULL
                    OR LOWER(p.address)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.emergencyContactName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.emergencyContactPhone)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.user.firstName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.user.lastName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.user.email)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.user.phone)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (:gender IS NULL OR p.gender = :gender)
            AND
                (:bloodGroup IS NULL OR p.bloodGroup = :bloodGroup)
            """)
    Page<Patient> searchPatients(
            @Param("keyword") String keyword,
            @Param("gender") Gender gender,
            @Param("bloodGroup") BloodGroup bloodGroup,
            Pageable pageable
    );
}
