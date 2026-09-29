package com.shms.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shms.entity.Medicine;

public interface MedicineRepository
        extends JpaRepository<Medicine, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Medicine> findByNameIgnoreCase(String name);

    @Query("""
            SELECT m
            FROM Medicine m
            WHERE
                (
                    :keyword IS NULL
                    OR LOWER(m.name)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(m.genericName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(m.manufacturer)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(m.category)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (:active IS NULL OR m.active = :active)
            AND
                (:expiryDate IS NULL OR m.expiryDate = :expiryDate)
            AND
                (
                    :stockAvailable IS NULL
                    OR (
                        :stockAvailable = true
                        AND m.stockQuantity > 0
                    )
                    OR (
                        :stockAvailable = false
                        AND m.stockQuantity = 0
                    )
                )
            """)
    Page<Medicine> searchMedicines(
            @Param("keyword") String keyword,
            @Param("active") Boolean active,
            @Param("expiryDate") LocalDate expiryDate,
            @Param("stockAvailable") Boolean stockAvailable,
            Pageable pageable
    );
}