package com.shms.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.shms.entity.enums.PaymentMethod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
public class BillRequest {

    @NotNull(message = "Bill date is required")
    private LocalDate billDate;

    @NotNull(message = "Total amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Total amount must be greater than zero"
    )
    private BigDecimal totalAmount;

    @NotNull(message = "Paid amount is required")
    @DecimalMin(
            value = "0.00",
            message = "Paid amount cannot be negative"
    )
    private BigDecimal paidAmount;

    private PaymentMethod paymentMethod;

    @Size(
            max = 500,
            message = "Description cannot exceed 500 characters"
    )
    private String description;

    @NotNull(message = "Patient ID is required")
    private Long patientId;
}