package com.shms.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class MedicineResponse {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String name;

    private String genericName;

    private String manufacturer;

    private String category;

    private BigDecimal price;

    private Integer stockQuantity;

    private Integer minimumStockLevel;

    private LocalDate expiryDate;

    private boolean active;
}