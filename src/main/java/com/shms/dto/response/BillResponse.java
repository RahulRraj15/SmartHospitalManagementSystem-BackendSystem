package com.shms.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.shms.entity.enums.PaymentMethod;
import com.shms.entity.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponse {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDate billDate;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal pendingAmount;

    private PaymentStatus paymentStatus;

    private PaymentMethod paymentMethod;

    private String description;

    private Long patientId;

    private String patientFirstName;

    private String patientLastName;

    private String patientEmail;
}