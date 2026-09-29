package com.shms.repository;

import com.shms.entity.Doctor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    boolean existsByDepartmentId(Long departmentId);

    // =====================================================
    // SEARCH + FILTERING + PAGINATION
    // =====================================================

    @Query("""
            SELECT d
            FROM Doctor d
            WHERE
                (
                    :keyword IS NULL
                    OR LOWER(d.specialization)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(d.qualification)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(d.experience)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(d.bio)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (:active IS NULL OR d.active = :active)
            """)
    Page<Doctor> searchDoctors(
            @Param("keyword") String keyword,
            @Param("active") Boolean active,
            Pageable pageable
    );
}