package com.shms.service;

import java.time.LocalDate;
import java.util.List;

import com.shms.dto.request.MedicineRequest;
import com.shms.dto.response.MedicineResponse;
import com.shms.dto.response.PageResponse;

public interface MedicineService {

    MedicineResponse createMedicine(
            MedicineRequest request
    );

    MedicineResponse getMedicineById(Long id);

    List<MedicineResponse> getAllMedicines();

    MedicineResponse updateMedicine(
            Long id,
            MedicineRequest request
    );

    void deleteMedicine(Long id);

    PageResponse<MedicineResponse> getAllMedicines(
            int page,
            int size,
            String sortBy,
            String direction
    );

    PageResponse<MedicineResponse> searchMedicines(
            String keyword,
            Boolean active,
            LocalDate expiryDate,
            Boolean stockAvailable,
            int page,
            int size,
            String sortBy,
            String direction
    );
}