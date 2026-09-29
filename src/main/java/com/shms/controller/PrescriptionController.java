package com.shms.controller;

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

import com.shms.dto.request.PrescriptionRequest;
import com.shms.dto.response.PageResponse;
import com.shms.dto.response.PrescriptionResponse;
import com.shms.service.PrescriptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@Tag(
        name = "Prescription Management",
        description = "APIs for creating, retrieving, updating and deleting prescriptions"
)
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    // =========================================================
    // CREATE PRESCRIPTION
    // =========================================================

    @PostMapping
    @Operation(
            summary = "Create a new prescription",
            description = "Creates a new prescription associated with an appointment."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Prescription created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid prescription data or validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Related appointment, doctor, patient or medicine not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<PrescriptionResponse>
    createPrescription(
            @Valid @RequestBody PrescriptionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        prescriptionService
                                .createPrescription(request)
                );
    }

    // =========================================================
    // GET ALL PRESCRIPTIONS
    // =========================================================

    @GetMapping
    @Operation(
            summary = "Get all prescriptions",
            description = "Retrieves all prescription records."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prescriptions retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<List<PrescriptionResponse>>
    getAllPrescriptions() {

        return ResponseEntity.ok(
                prescriptionService
                        .getAllPrescriptions()
        );
    }

    // =========================================================
    // GET PRESCRIPTION BY ID
    // =========================================================

    @GetMapping("/{id}")
    @Operation(
            summary = "Get prescription by ID",
            description = "Retrieves a specific prescription using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prescription retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid prescription ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Prescription not found"
            )
    })
    public ResponseEntity<PrescriptionResponse>
    getPrescriptionById(

            @Parameter(
                    description = "Unique ID of the prescription",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        return ResponseEntity.ok(
                prescriptionService
                        .getPrescriptionById(id)
        );
    }

    // =========================================================
    // GET PRESCRIPTION BY APPOINTMENT
    // =========================================================

    @GetMapping("/appointment/{appointmentId}")
    @Operation(
            summary = "Get prescription by appointment",
            description = "Retrieves the prescription associated with a specific appointment."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prescription retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Prescription or appointment not found"
            )
    })
    public ResponseEntity<PrescriptionResponse>
    getPrescriptionByAppointment(

            @Parameter(
                    description = "Unique ID of the appointment",
                    required = true,
                    example = "1"
            )
            @PathVariable Long appointmentId) {

        return ResponseEntity.ok(
                prescriptionService
                        .getPrescriptionByAppointment(
                                appointmentId
                        )
        );
    }

    // =========================================================
    // UPDATE PRESCRIPTION
    // =========================================================

    @PutMapping("/{id}")
    @Operation(
            summary = "Update prescription",
            description = "Updates an existing prescription using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prescription updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid prescription data or validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Prescription or related resource not found"
            )
    })
    public ResponseEntity<PrescriptionResponse>
    updatePrescription(

            @Parameter(
                    description = "Unique ID of the prescription to update",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody PrescriptionRequest request) {

        return ResponseEntity.ok(
                prescriptionService
                        .updatePrescription(id, request)
        );
    }

    // =========================================================
    // DELETE PRESCRIPTION
    // =========================================================

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete prescription",
            description = "Deletes an existing prescription using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Prescription deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid prescription ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Prescription not found"
            )
    })
    public ResponseEntity<Void> deletePrescription(

            @Parameter(
                    description = "Unique ID of the prescription to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        prescriptionService.deletePrescription(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // PAGINATION + SORTING
    // =========================================================

    @GetMapping("/page")
    @Operation(
            summary = "Get prescriptions with pagination and sorting",
            description = "Retrieves prescriptions using pagination and sorting. "
                    + "Default page is 0, default page size is 5, "
                    + "and default sorting is by ID in ascending order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prescriptions retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    public ResponseEntity<PageResponse<PrescriptionResponse>>
    getPrescriptionsWithPaginationAndSorting(

            @Parameter(
                    description = "Page number, starting from 0",
                    example = "0",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    description = "Number of prescriptions per page",
                    example = "5",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    description = "Field used for sorting. "
                            + "Allowed values: id, prescriptionDate",
                    example = "prescriptionDate",
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
                prescriptionService.getAllPrescriptions(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }
}