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

import com.shms.dto.request.MedicalReportRequest;
import com.shms.dto.response.MedicalReportResponse;
import com.shms.dto.response.PageResponse;
import com.shms.service.MedicalReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/medical-reports")
@RequiredArgsConstructor
@Tag(
        name = "Medical Report Management",
        description = "APIs for managing patient medical reports, diagnoses, treatments and observations"
)
public class MedicalReportController {

    private final MedicalReportService medicalReportService;

    // =========================================================
    // CREATE MEDICAL REPORT
    // =========================================================

    @PostMapping
    @Operation(
            summary = "Create a new medical report",
            description = "Creates a new medical report for a patient."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Medical report created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid medical report data or validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Related patient or doctor not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<MedicalReportResponse>
    createMedicalReport(
            @Valid @RequestBody MedicalReportRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        medicalReportService
                                .createMedicalReport(request)
                );
    }

    // =========================================================
    // GET ALL MEDICAL REPORTS
    // =========================================================

    @GetMapping
    @Operation(
            summary = "Get all medical reports",
            description = "Retrieves all medical reports from the hospital system."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medical reports retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<List<MedicalReportResponse>>
    getAllMedicalReports() {

        return ResponseEntity.ok(
                medicalReportService
                        .getAllMedicalReports()
        );
    }

    // =========================================================
    // GET MEDICAL REPORT BY ID
    // =========================================================

    @GetMapping("/{id}")
    @Operation(
            summary = "Get medical report by ID",
            description = "Retrieves a specific medical report using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medical report retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid medical report ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Medical report not found"
            )
    })
    public ResponseEntity<MedicalReportResponse>
    getMedicalReportById(

            @Parameter(
                    description = "Unique ID of the medical report",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        return ResponseEntity.ok(
                medicalReportService
                        .getMedicalReportById(id)
        );
    }

    // =========================================================
    // GET MEDICAL REPORTS BY PATIENT
    // =========================================================

    @GetMapping("/patient/{patientId}")
    @Operation(
            summary = "Get medical reports by patient",
            description = "Retrieves all medical reports associated with a specific patient."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient medical reports retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid patient ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found"
            )
    })
    public ResponseEntity<List<MedicalReportResponse>>
    getMedicalReportsByPatient(

            @Parameter(
                    description = "Unique ID of the patient",
                    required = true,
                    example = "1"
            )
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                medicalReportService
                        .getMedicalReportsByPatient(
                                patientId
                        )
        );
    }

    // =========================================================
    // GET MEDICAL REPORTS BY DOCTOR
    // =========================================================

    @GetMapping("/doctor/{doctorId}")
    @Operation(
            summary = "Get medical reports by doctor",
            description = "Retrieves all medical reports created by a specific doctor."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor medical reports retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid doctor ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    public ResponseEntity<List<MedicalReportResponse>>
    getMedicalReportsByDoctor(

            @Parameter(
                    description = "Unique ID of the doctor",
                    required = true,
                    example = "1"
            )
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                medicalReportService
                        .getMedicalReportsByDoctor(
                                doctorId
                        )
        );
    }

    // =========================================================
    // UPDATE MEDICAL REPORT
    // =========================================================

    @PutMapping("/{id}")
    @Operation(
            summary = "Update medical report",
            description = "Updates an existing medical report using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medical report updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid medical report data or validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Medical report or related resource not found"
            )
    })
    public ResponseEntity<MedicalReportResponse>
    updateMedicalReport(

            @Parameter(
                    description = "Unique ID of the medical report to update",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody MedicalReportRequest request) {

        return ResponseEntity.ok(
                medicalReportService
                        .updateMedicalReport(
                                id,
                                request
                        )
        );
    }

    // =========================================================
    // DELETE MEDICAL REPORT
    // =========================================================

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete medical report",
            description = "Deletes an existing medical report using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Medical report deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid medical report ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Medical report not found"
            )
    })
    public ResponseEntity<Void>
    deleteMedicalReport(

            @Parameter(
                    description = "Unique ID of the medical report to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        medicalReportService.deleteMedicalReport(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // PAGINATION + SORTING
    // =========================================================

    @GetMapping("/page")
    @Operation(
            summary = "Get medical reports with pagination and sorting",
            description = "Retrieves medical reports using pagination and sorting. "
                    + "Default page is 0, default page size is 5, "
                    + "and default sorting is by ID in ascending order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medical reports retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    public ResponseEntity<PageResponse<MedicalReportResponse>>
    getMedicalReportsWithPaginationAndSorting(

            @Parameter(
                    description = "Page number, starting from 0",
                    example = "0",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    description = "Number of medical reports per page",
                    example = "5",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    description = "Field used for sorting. "
                            + "Allowed values: id, reportDate, diagnosis, "
                            + "symptoms, treatment, observations, reportFileUrl",
                    example = "reportDate",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "id")
            String sortBy,

            @Parameter(
                    description = "Sorting direction: asc or desc",
                    example = "desc",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "asc")
            String direction) {

        return ResponseEntity.ok(
                medicalReportService.getAllMedicalReports(
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
            summary = "Search and filter medical reports",
            description = "Searches medical reports using a keyword and supports "
                    + "filtering by report date, patient ID and doctor ID. "
                    + "Pagination and sorting are also supported."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medical report search completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search, filter, pagination or sorting parameters"
            )
    })
    public ResponseEntity<PageResponse<MedicalReportResponse>>
    searchMedicalReports(

            @Parameter(
                    description = "Search keyword applied to diagnosis, symptoms, "
                            + "treatment, observations, report file URL and patient/doctor information",
                    example = "fever",
                    in = ParameterIn.QUERY
            )
            @RequestParam(required = false)
            String keyword,

            @Parameter(
                    description = "Filter medical reports by report date",
                    example = "2026-09-14",
                    in = ParameterIn.QUERY
            )
            @RequestParam(required = false)
            LocalDate reportDate,

            @Parameter(
                    description = "Filter medical reports by patient ID",
                    example = "1",
                    in = ParameterIn.QUERY
            )
            @RequestParam(required = false)
            Long patientId,

            @Parameter(
                    description = "Filter medical reports by doctor ID",
                    example = "1",
                    in = ParameterIn.QUERY
            )
            @RequestParam(required = false)
            Long doctorId,

            @Parameter(
                    description = "Page number, starting from 0",
                    example = "0",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    description = "Number of medical reports per page",
                    example = "5",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    description = "Field used for sorting. "
                            + "Allowed values: id, reportDate, diagnosis, "
                            + "symptoms, treatment, observations, reportFileUrl",
                    example = "reportDate",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "id")
            String sortBy,

            @Parameter(
                    description = "Sorting direction: asc or desc",
                    example = "desc",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "asc")
            String direction) {

        return ResponseEntity.ok(
                medicalReportService.searchMedicalReports(
                        keyword,
                        reportDate,
                        patientId,
                        doctorId,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }
}