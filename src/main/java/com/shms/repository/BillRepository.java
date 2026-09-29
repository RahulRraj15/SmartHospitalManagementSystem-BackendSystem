package com.shms.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shms.entity.Bill;
import com.shms.entity.enums.PaymentMethod;
import com.shms.entity.enums.PaymentStatus;

public interface BillRepository
        extends JpaRepository<Bill, Long> {

    List<Bill> findByPatientId(Long patientId);

    @Query("""
            SELECT b
            FROM Bill b
            WHERE
                (
                    :keyword IS NULL
                    OR LOWER(b.description)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(b.patient.address)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(b.patient.emergencyContactName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(b.patient.emergencyContactPhone)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(b.patient.user.firstName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(b.patient.user.lastName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(b.patient.user.email)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(b.patient.user.phone)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (
                    :paymentStatus IS NULL
                    OR b.paymentStatus = :paymentStatus
                )
            AND
                (
                    :paymentMethod IS NULL
                    OR b.paymentMethod = :paymentMethod
                )
            AND
                (
                    :billDate IS NULL
                    OR b.billDate = :billDate
                )
            """)
    Page<Bill> searchBills(
            @Param("keyword") String keyword,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("billDate") LocalDate billDate,
            Pageable pageable
    );
}