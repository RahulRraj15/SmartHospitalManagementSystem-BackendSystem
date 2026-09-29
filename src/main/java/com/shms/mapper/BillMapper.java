package com.shms.mapper;

import java.math.BigDecimal;

import com.shms.dto.request.BillRequest;
import com.shms.dto.response.BillResponse;
import com.shms.entity.Bill;
import com.shms.entity.Patient;

public class BillMapper {

    private BillMapper() {
    }

    public static Bill toEntity(
            BillRequest request,
            Patient patient) {

        return Bill.builder()
                .billDate(request.getBillDate())
                .totalAmount(request.getTotalAmount())
                .paidAmount(request.getPaidAmount())
                .paymentMethod(request.getPaymentMethod())
                .description(request.getDescription())
                .patient(patient)
                .build();
    }

    public static BillResponse toResponse(
            Bill bill) {

        Patient patient = bill.getPatient();

        BigDecimal pendingAmount =
                bill.getTotalAmount()
                        .subtract(bill.getPaidAmount());

        return BillResponse.builder()
                .id(bill.getId())
                .createdAt(bill.getCreatedAt())
                .updatedAt(bill.getUpdatedAt())
                .billDate(bill.getBillDate())
                .totalAmount(bill.getTotalAmount())
                .paidAmount(bill.getPaidAmount())
                .pendingAmount(pendingAmount)
                .paymentStatus(bill.getPaymentStatus())
                .paymentMethod(bill.getPaymentMethod())
                .description(bill.getDescription())
                .patientId(patient.getId())
                .patientFirstName(
                        patient.getUser().getFirstName()
                )
                .patientLastName(
                        patient.getUser().getLastName()
                )
                .patientEmail(
                        patient.getUser().getEmail()
                )
                .build();
    }
}