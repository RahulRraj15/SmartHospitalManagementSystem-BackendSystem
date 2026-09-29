
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

import com.shms.dto.request.DoctorRequest;
import com.shms.dto.response.DoctorResponse;
import com.shms.dto.response.PageResponse;
import com.shms.service.DoctorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(
        name = "Doctor Management",
        description = "APIs for creating, retrieving, updating, "
                + "searching and deleting hospital doctors"
)
public class DoctorController {

    private final DoctorService doctorService;

    // =====================================================
    // CREATE DOCTOR
    // =====================================================

    @Operation(
            summary = "Create a new doctor",
            description = "Creates a new doctor and returns "
                    + "the created doctor details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Doctor created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid doctor data"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(
            @Valid @RequestBody DoctorRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        doctorService.createDoctor(request)
                );
    }

    // =====================================================
    // GET DOCTOR BY ID
    // =====================================================

    @Operation(
            summary = "Get doctor by ID",
            description = "Retrieves a single doctor using "
                    + "the doctor ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor found successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid doctor ID"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the doctor",
                    required = true,
                    example = "1",
                    in = ParameterIn.PATH
            )
            @PathVariable Long id) {

        return ResponseEntity.ok(
                doctorService.getDoctorById(id)
        );
    }

    // =====================================================
    // GET ALL DOCTORS
    // =====================================================

    @Operation(
            summary = "Get all doctors",
            description = "Retrieves all doctors without pagination."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctors retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<DoctorResponse>>
    getAllDoctors() {

        return ResponseEntity.ok(
                doctorService.getAllDoctors()
        );
    }

    // =====================================================
    // PAGINATION + SORTING
    // =====================================================

    @Operation(
            summary = "Get doctors with pagination and sorting",
            description = "Retrieves doctors using pagination "
                    + "and sorting. Page numbering starts from 0."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctors retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    @GetMapping("/page")
    public ResponseEntity<PageResponse<DoctorResponse>>
    getDoctorsWithPaginationAndSorting(

            @Parameter(
                    name = "page",
                    description = "Page number. Starts from 0.",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    name = "size",
                    description = "Number of doctors per page. "
                            + "Maximum allowed size is 50.",
                    example = "5"
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    name = "sortBy",
                    description = "Field used for sorting. "
                            + "Example: id, specialization, "
                            + "qualification, experience",
                    example = "specialization"
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
                doctorService.getAllDoctors(
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
            summary = "Search and filter doctors",
            description = "Searches doctors using a keyword and "
                    + "optionally filters them by active status. "
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
    public ResponseEntity<PageResponse<DoctorResponse>>
    searchDoctors(

            @Parameter(
                    name = "keyword",
                    description = "Search keyword applied to doctor "
                            + "specialization, qualification, experience "
                            + "and bio",
                    example = "Cardiology"
            )
            @RequestParam(required = false)
            String keyword,

            @Parameter(
                    name = "active",
                    description = "Filter doctors by active status",
                    example = "true"
            )
            @RequestParam(required = false)
            Boolean active,

            @Parameter(
                    name = "page",
                    description = "Page number. Starts from 0.",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    name = "size",
                    description = "Number of doctors per page. "
                            + "Maximum allowed size is 50.",
                    example = "5"
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    name = "sortBy",
                    description = "Field used for sorting",
                    example = "specialization"
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
                doctorService.searchDoctors(
                        keyword,
                        active,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    // =====================================================
    // UPDATE DOCTOR
    // =====================================================

    @Operation(
            summary = "Update an existing doctor",
            description = "Updates an existing doctor using "
                    + "the doctor ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid doctor data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the doctor to update",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody DoctorRequest request) {

        return ResponseEntity.ok(
                doctorService.updateDoctor(
                        id,
                        request
                )
        );
    }

    // =====================================================
    // DELETE DOCTOR
    // =====================================================

    @Operation(
            summary = "Delete a doctor",
            description = "Deletes an existing doctor using "
                    + "the doctor ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Doctor deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid doctor ID"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the doctor to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        doctorService.deleteDoctor(id);

        return ResponseEntity.noContent().build();
    }
}
