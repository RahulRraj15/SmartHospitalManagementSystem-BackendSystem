package com.shms.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shms.dto.request.MedicineRequest;
import com.shms.dto.response.MedicineResponse;
import com.shms.dto.response.PageResponse;
import com.shms.service.MedicineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medicines")
@Tag(
        name = "Medicine Management",
        description = "APIs for managing hospital medicines, inventory, expiry dates and stock"
)
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(
            MedicineService medicineService) {

        this.medicineService = medicineService;
    }

    // =========================================================
    // CREATE MEDICINE
    // =========================================================

    @PostMapping
    @Operation(
            summary = "Create a new medicine",
            description = "Creates a new medicine record in the hospital medicine inventory."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Medicine created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid medicine data or validation failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<MedicineResponse>
            createMedicine(
                    @Valid
                    @RequestBody
                    MedicineRequest request) {

        MedicineResponse response =
                medicineService.createMedicine(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET ALL MEDICINES
    // =========================================================

    @GetMapping
    @Operation(
            summary = "Get all medicines",
            description = "Retrieves all medicine records from the hospital inventory."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medicines retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<List<MedicineResponse>>
            getAllMedicines() {

        return ResponseEntity.ok(
                medicineService.getAllMedicines()
        );
    }

    // =========================================================
    // GET MEDICINE BY ID
    // =========================================================

    @GetMapping("/{id}")
    @Operation(
            summary = "Get medicine by ID",
            description = "Retrieves a specific medicine using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medicine retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid medicine ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Medicine not found"
            )
    })
    public ResponseEntity<MedicineResponse>
            getMedicineById(

                    @Parameter(
                            description = "Unique ID of the medicine",
                            required = true,
                            example = "1"
                    )
                    @PathVariable Long id) {

        return ResponseEntity.ok(
                medicineService.getMedicineById(id)
        );
    }

    // =========================================================
    // UPDATE MEDICINE
    // =========================================================

    @PutMapping("/{id}")
    @Operation(
            summary = "Update medicine",
            description = "Updates an existing medicine record using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medicine updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid medicine data or validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Medicine not found"
            )
    })
    public ResponseEntity<MedicineResponse>
            updateMedicine(

                    @Parameter(
                            description = "Unique ID of the medicine to update",
                            required = true,
                            example = "1"
                    )
                    @PathVariable Long id,

                    @Valid
                    @RequestBody
                    MedicineRequest request) {

        return ResponseEntity.ok(
                medicineService.updateMedicine(
                        id,
                        request
                )
        );
    }

    // =========================================================
    // DELETE MEDICINE
    // =========================================================

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete medicine",
            description = "Deletes an existing medicine record using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Medicine deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid medicine ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Medicine not found"
            )
    })
    public ResponseEntity<Void> deleteMedicine(

            @Parameter(
                    description = "Unique ID of the medicine to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        medicineService.deleteMedicine(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // PAGINATION + SORTING
    // =========================================================

    @GetMapping("/page")
    @Operation(
            summary = "Get medicines with pagination and sorting",
            description = "Retrieves medicines using pagination and sorting. "
                    + "Default page is 0, default page size is 5, "
                    + "default sorting is by ID in ascending order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medicines retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    public ResponseEntity<PageResponse<MedicineResponse>>
            getMedicinesWithPaginationAndSorting(

                    @Parameter(
                            description = "Page number, starting from 0",
                            example = "0",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(defaultValue = "0")
                    int page,

                    @Parameter(
                            description = "Number of medicines per page",
                            example = "5",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(defaultValue = "5")
                    int size,

                    @Parameter(
                            description = "Field used for sorting. "
                                    + "Allowed values: id, name, genericName, "
                                    + "manufacturer, category, price, "
                                    + "stockQuantity, minimumStockLevel, "
                                    + "expiryDate, active",
                            example = "name",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(defaultValue = "id")
                    String sortBy,

                    @Parameter(
                            description = "Sorting direction: asc or desc",
                            example = "asc",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(defaultValue = "asc")
                    String direction) {

        return ResponseEntity.ok(
                medicineService.getAllMedicines(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    // =========================================================
    // SEARCH + FILTER + PAGINATION + SORTING
    // =========================================================

    @GetMapping("/search")
    @Operation(
            summary = "Search and filter medicines",
            description = "Searches medicines using a keyword and supports "
                    + "filtering by active status, expiry date and stock availability. "
                    + "Pagination and sorting are also supported."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medicine search completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search, filter, pagination or sorting parameters"
            )
    })
    public ResponseEntity<PageResponse<MedicineResponse>>
            searchMedicines(

                    @Parameter(
                            description = "Search keyword applied to medicine name, "
                                    + "generic name, manufacturer and category",
                            example = "Paracetamol",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(required = false)
                    String keyword,

                    @Parameter(
                            description = "Filter medicines by active status",
                            example = "true",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(required = false)
                    Boolean active,

                    @Parameter(
                            description = "Filter medicines by expiry date",
                            example = "2027-12-31",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(required = false)
                    LocalDate expiryDate,

                    @Parameter(
                            description = "Filter medicines based on stock availability",
                            example = "true",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(required = false)
                    Boolean stockAvailable,

                    @Parameter(
                            description = "Page number, starting from 0",
                            example = "0",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(defaultValue = "0")
                    int page,

                    @Parameter(
                            description = "Number of medicines per page",
                            example = "5",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(defaultValue = "5")
                    int size,

                    @Parameter(
                            description = "Field used for sorting. "
                                    + "Allowed values: id, name, genericName, "
                                    + "manufacturer, category, price, "
                                    + "stockQuantity, minimumStockLevel, "
                                    + "expiryDate, active",
                            example = "expiryDate",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(defaultValue = "id")
                    String sortBy,

                    @Parameter(
                            description = "Sorting direction: asc or desc",
                            example = "asc",
                            in = ParameterIn.QUERY
                    )
                    @RequestParam(defaultValue = "asc")
                    String direction) {

        return ResponseEntity.ok(
                medicineService.searchMedicines(
                        keyword,
                        active,
                        expiryDate,
                        stockAvailable,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }
}