
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

import com.shms.dto.request.PatientRequest;
import com.shms.dto.response.PageResponse;
import com.shms.dto.response.PatientResponse;
import com.shms.entity.enums.BloodGroup;
import com.shms.entity.enums.Gender;
import com.shms.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
@Tag(
        name = "Patient Management",
        description = "APIs for creating, retrieving, updating, "
                + "searching and deleting hospital patients"
)
public class PatientController {

    private final PatientService patientService;

    public PatientController(
            PatientService patientService) {

        this.patientService = patientService;
    }

    // =====================================================
    // CREATE PATIENT
    // =====================================================

    @Operation(
            summary = "Create a new patient",
            description = "Creates a new patient and returns "
                    + "the created patient details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Patient created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid patient data"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody PatientRequest request) {

        PatientResponse response =
                patientService.createPatient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // GET ALL PATIENTS
    // =====================================================

    @Operation(
            summary = "Get all patients",
            description = "Retrieves all patients without pagination."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<PatientResponse>>
    getAllPatients() {

        return ResponseEntity.ok(
                patientService.getAllPatients()
        );
    }

    // =====================================================
    // GET PATIENT BY ID
    // =====================================================

    @Operation(
            summary = "Get patient by ID",
            description = "Retrieves a single patient using "
                    + "the patient ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient found successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid patient ID"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse>
    getPatientById(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the patient",
                    required = true,
                    example = "1",
                    in = ParameterIn.PATH
            )
            @PathVariable Long id) {

        return ResponseEntity.ok(
                patientService.getPatientById(id)
        );
    }

    // =====================================================
    // UPDATE PATIENT
    // =====================================================

    @Operation(
            summary = "Update an existing patient",
            description = "Updates an existing patient using "
                    + "the patient ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid patient data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse>
    updatePatient(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the patient to update",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody PatientRequest request) {

        return ResponseEntity.ok(
                patientService.updatePatient(
                        id,
                        request
                )
        );
    }

    // =====================================================
    // DELETE PATIENT
    // =====================================================

    @Operation(
            summary = "Delete a patient",
            description = "Deletes an existing patient using "
                    + "the patient ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Patient deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid patient ID"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the patient to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        patientService.deletePatient(id);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // PAGINATION + SORTING
    // =====================================================

    @Operation(
            summary = "Get patients with pagination and sorting",
            description = "Retrieves patients using pagination "
                    + "and sorting. Page numbering starts from 0."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    @GetMapping("/page")
    public ResponseEntity<PageResponse<PatientResponse>>
    getPatientsWithPaginationAndSorting(

            @Parameter(
                    name = "page",
                    description = "Page number. Starts from 0.",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    name = "size",
                    description = "Number of patients per page. "
                            + "Maximum allowed size is 50.",
                    example = "5"
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    name = "sortBy",
                    description = "Field used for sorting. "
                            + "Example: id, dateOfBirth, gender, bloodGroup",
                    example = "dateOfBirth"
            )
            @RequestParam(defaultValue = "id")
            String sortBy,

            @Parameter(
                    name = "direction",
                    description = "Sorting direction: asc or desc",
                    example = "asc"
            )
            @RequestParam(defaultValue = "asc")
            String direction) {

        return ResponseEntity.ok(
                patientService.getAllPatients(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    // =====================================================
    // SEARCH + FILTERING
    // =====================================================

    @Operation(
            summary = "Search and filter patients",
            description = "Searches patients using a keyword and "
                    + "optionally filters them by gender and blood group. "
                    + "Supports pagination and sorting."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search results retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search, filter, pagination "
                            + "or sorting parameters"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<PageResponse<PatientResponse>>
    searchPatients(

            @Parameter(
                    name = "keyword",
                    description = "Search keyword applied to patient "
                            + "address, emergency contact details and "
                            + "user information",
                    example = "Rahul"
            )
            @RequestParam(required = false)
            String keyword,

            @Parameter(
                    name = "gender",
                    description = "Filter patients by gender",
                    example = "MALE"
            )
            @RequestParam(required = false)
            Gender gender,

            @Parameter(
                    name = "bloodGroup",
                    description = "Filter patients by blood group",
                    example = "O_POSITIVE"
            )
            @RequestParam(required = false)
            BloodGroup bloodGroup,

            @Parameter(
                    name = "page",
                    description = "Page number. Starts from 0.",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    name = "size",
                    description = "Number of patients per page. "
                            + "Maximum allowed size is 50.",
                    example = "5"
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    name = "sortBy",
                    description = "Field used for sorting",
                    example = "dateOfBirth"
            )
            @RequestParam(defaultValue = "id")
            String sortBy,

            @Parameter(
                    name = "direction",
                    description = "Sorting direction: asc or desc",
                    example = "asc"
            )
            @RequestParam(defaultValue = "asc")
            String direction) {

        return ResponseEntity.ok(
                patientService.searchPatients(
                        keyword,
                        gender,
                        bloodGroup,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }
}
