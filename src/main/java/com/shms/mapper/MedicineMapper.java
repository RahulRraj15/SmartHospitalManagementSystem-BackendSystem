package com.shms.mapper;

import com.shms.dto.request.MedicineRequest;
import com.shms.dto.response.MedicineResponse;
import com.shms.entity.Medicine;

public class MedicineMapper {

    private MedicineMapper() {
    }

    public static Medicine toEntity(
            MedicineRequest request) {

        return Medicine.builder()
                .name(request.getName())
                .genericName(request.getGenericName())
                .manufacturer(request.getManufacturer())
                .category(request.getCategory())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .minimumStockLevel(
                        request.getMinimumStockLevel()
                )
                .expiryDate(request.getExpiryDate())
                .active(true)
                .build();
    }

    public static MedicineResponse toResponse(
            Medicine medicine) {

        return MedicineResponse.builder()
                .id(medicine.getId())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .name(medicine.getName())
                .genericName(medicine.getGenericName())
                .manufacturer(medicine.getManufacturer())
                .category(medicine.getCategory())
                .price(medicine.getPrice())
                .stockQuantity(
                        medicine.getStockQuantity()
                )
                .minimumStockLevel(
                        medicine.getMinimumStockLevel()
                )
                .expiryDate(medicine.getExpiryDate())
                .active(medicine.isActive())
                .build();
    }
}