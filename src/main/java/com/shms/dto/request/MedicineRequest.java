package com.shms.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class MedicineRequest {

    @NotBlank(message = "Medicine name is required")
    @Size(
            min = 2,
            max = 150,
            message = "Medicine name must be between 2 and 150 characters"
    )
    private String name;

    @Size(
            max = 150,
            message = "Generic name cannot exceed 150 characters"
    )
    private String genericName;

    @Size(
            max = 150,
            message = "Manufacturer cannot exceed 150 characters"
    )
    private String manufacturer;

    @Size(
            max = 100,
            message = "Category cannot exceed 100 characters"
    )
    private String category;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than zero"
    )
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(
            value = 0,
            message = "Stock quantity cannot be negative"
    )
    private Integer stockQuantity;

    @NotNull(message = "Minimum stock level is required")
    @Min(
            value = 0,
            message = "Minimum stock level cannot be negative"
    )
    private Integer minimumStockLevel;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;
}