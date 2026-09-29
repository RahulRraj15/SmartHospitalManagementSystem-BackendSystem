
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

import com.shms.dto.request.AppointmentRequest;
import com.shms.dto.response.AppointmentResponse;
import com.shms.dto.response.PageResponse;
import com.shms.entity.enums.AppointmentStatus;
import com.shms.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
@Tag(
        name = "Appointment Management",
        description = "APIs for creating, retrieving, updating, "
                + "searching and deleting hospital appointments"
)
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService) {

        this.appointmentService = appointmentService;
    }

    // =====================================================
    // CREATE APPOINTMENT
    // =====================================================

    @Operation(
            summary = "Create a new appointment",
            description = "Creates a new patient appointment and "
                    + "returns the created appointment details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Appointment created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment data or "
                            + "appointment conflict"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor or patient not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PostMapping
    public ResponseEntity<AppointmentResponse>
            createAppointment(
                    @Valid
                    @RequestBody
                    AppointmentRequest request) {

        AppointmentResponse response =
                appointmentService.createAppointment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // GET ALL APPOINTMENTS
    // =====================================================

    @Operation(
            summary = "Get all appointments",
            description = "Retrieves all hospital appointments "
                    + "without pagination."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>>
            getAllAppointments() {

        return ResponseEntity.ok(
                appointmentService.getAllAppointments()
        );
    }

    // =====================================================
    // GET APPOINTMENT BY ID
    // =====================================================

    @Operation(
            summary = "Get appointment by ID",
            description = "Retrieves a single appointment using "
                    + "the appointment ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment found successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment ID"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse>
            getAppointmentById(

                    @Parameter(
                            name = "id",
                            description = "Unique ID of the appointment",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH
                    )
                    @PathVariable Long id) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentById(id)
        );
    }

    // =====================================================
    // UPDATE APPOINTMENT
    // =====================================================

    @Operation(
            summary = "Update an existing appointment",
            description = "Updates an existing appointment using "
                    + "the appointment ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment data or "
                            + "appointment conflict"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment, doctor or patient not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse>
            updateAppointment(

                    @Parameter(
                            name = "id",
                            description = "Unique ID of the appointment "
                                    + "to update",
                            required = true,
                            example = "1"
                    )
                    @PathVariable Long id,

                    @Valid
                    @RequestBody
                    AppointmentRequest request) {

        return ResponseEntity.ok(
                appointmentService.updateAppointment(
                        id,
                        request
                )
        );
    }

    // =====================================================
    // DELETE APPOINTMENT
    // =====================================================

    @Operation(
            summary = "Delete an appointment",
            description = "Deletes an existing appointment using "
                    + "the appointment ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Appointment deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment ID"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the appointment to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        appointmentService.deleteAppointment(id);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // PAGINATION + SORTING
    // =====================================================

    @Operation(
            summary = "Get appointments with pagination and sorting",
            description = "Retrieves appointments using pagination "
                    + "and sorting. Page numbering starts from 0."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    @GetMapping("/page")
    public ResponseEntity<PageResponse<AppointmentResponse>>
            getAppointmentsWithPaginationAndSorting(

                    @Parameter(
                            name = "page",
                            description = "Page number. Starts from 0.",
                            example = "0"
                    )
                    @RequestParam(defaultValue = "0")
                    int page,

                    @Parameter(
                            name = "size",
                            description = "Number of appointments per page. "
                                    + "Maximum allowed size is 50.",
                            example = "5"
                    )
                    @RequestParam(defaultValue = "5")
                    int size,

                    @Parameter(
                            name = "sortBy",
                            description = "Field used for sorting. "
                                    + "Example: id, appointmentDate, "
                                    + "appointmentTime, status",
                            example = "appointmentDate"
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
                appointmentService.getAllAppointments(
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
            summary = "Search and filter appointments",
            description = "Searches appointments using a keyword "
                    + "and optionally filters by appointment status "
                    + "and appointment date. Supports pagination "
                    + "and sorting."
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
    public ResponseEntity<PageResponse<AppointmentResponse>>
            searchAppointments(

                    @Parameter(
                            name = "keyword",
                            description = "Search keyword applied to "
                                    + "appointment reason, notes, doctor "
                                    + "and patient information",
                            example = "fever"
                    )
                    @RequestParam(required = false)
                    String keyword,

                    @Parameter(
                            name = "status",
                            description = "Filter appointments by status",
                            example = "SCHEDULED"
                    )
                    @RequestParam(required = false)
                    AppointmentStatus status,

                    @Parameter(
                            name = "appointmentDate",
                            description = "Filter appointments by date "
                                    + "using yyyy-MM-dd format",
                            example = "2026-09-20"
                    )
                    @RequestParam(required = false)
                    LocalDate appointmentDate,

                    @Parameter(
                            name = "page",
                            description = "Page number. Starts from 0.",
                            example = "0"
                    )
                    @RequestParam(defaultValue = "0")
                    int page,

                    @Parameter(
                            name = "size",
                            description = "Number of appointments per page. "
                                    + "Maximum allowed size is 50.",
                            example = "5"
                    )
                    @RequestParam(defaultValue = "5")
                    int size,

                    @Parameter(
                            name = "sortBy",
                            description = "Field used for sorting",
                            example = "appointmentDate"
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
                appointmentService.searchAppointments(
                        keyword,
                        status,
                        appointmentDate,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }
}
