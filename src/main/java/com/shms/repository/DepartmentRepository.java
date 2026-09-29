package com.shms.repository;

import com.shms.entity.Department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    boolean existsByName(String name);

    // =========================================================
    // SEARCH + FILTERING + PAGINATION
    // =========================================================

    @Query("""
            SELECT d
            FROM Department d
            WHERE
                (
                    :keyword IS NULL
                    OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (:active IS NULL OR d.active = :active)
            """)
    Page<Department> searchDepartments(
            @Param("keyword") String keyword,
            @Param("active") Boolean active,
            Pageable pageable
    );
}