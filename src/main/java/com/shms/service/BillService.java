package com.shms.service;

import java.time.LocalDate;
import java.util.List;

import com.shms.dto.request.BillRequest;
import com.shms.dto.response.BillResponse;
import com.shms.dto.response.PageResponse;
import com.shms.entity.enums.PaymentMethod;
import com.shms.entity.enums.PaymentStatus;

public interface BillService {

    BillResponse createBill(BillRequest request);

    BillResponse getBillById(Long id);

    List<BillResponse> getAllBills();

    List<BillResponse> getBillsByPatient(Long patientId);

    BillResponse updateBill(
            Long id,
            BillRequest request
    );

    void deleteBill(Long id);

    PageResponse<BillResponse> getAllBills(
            int page,
            int size,
            String sortBy,
            String direction
    );

    PageResponse<BillResponse> searchBills(
            String keyword,
            PaymentStatus paymentStatus,
            PaymentMethod paymentMethod,
            LocalDate billDate,
            int page,
            int size,
            String sortBy,
            String direction
    );
}