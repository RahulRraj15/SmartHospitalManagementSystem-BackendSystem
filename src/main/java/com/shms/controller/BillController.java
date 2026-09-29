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

import com.shms.dto.request.BillRequest;
import com.shms.dto.response.BillResponse;
import com.shms.dto.response.PageResponse;
import com.shms.entity.enums.PaymentMethod;
import com.shms.entity.enums.PaymentStatus;
import com.shms.service.BillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bills")
@Tag(
        name = "Billing Management",
        description = "APIs for managing hospital bills, payments and billing records"
)
public class BillController {

    private final BillService billService;

    public BillController(
            BillService billService) {

        this.billService = billService;
    }

    // =========================================================
    // CREATE BILL
    // =========================================================

    @PostMapping
    @Operation(
            summary = "Create a new bill",
            description = "Creates a new billing record for a patient."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Bill created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid bill data or validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<BillResponse> createBill(
            @Valid
            @RequestBody
            BillRequest request) {

        BillResponse response =
                billService.createBill(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET ALL BILLS
    // =========================================================

    @GetMapping
    @Operation(
            summary = "Get all bills",
            description = "Retrieves all billing records from the hospital system."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bills retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<List<BillResponse>>
            getAllBills() {

        return ResponseEntity.ok(
                billService.getAllBills()
        );
    }

    // =========================================================
    // GET BILL BY ID
    // =========================================================

    @GetMapping("/{id}")
    @Operation(
            summary = "Get bill by ID",
            description = "Retrieves a specific bill using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bill retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid bill ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bill not found"
            )
    })
    public ResponseEntity<BillResponse>
            getBillById(

                    @Parameter(
                            description = "Unique ID of the bill",
                            required = true,
                            example = "1"
                    )
                    @PathVariable Long id) {

        return ResponseEntity.ok(
                billService.getBillById(id)
        );
    }

    // =========================================================
    // GET BILLS BY PATIENT
    // =========================================================

    @GetMapping("/patient/{patientId}")
    @Operation(
            summary = "Get bills by patient",
            description = "Retrieves all billing records associated with a specific patient."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient bills retrieved successfully"
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
    public ResponseEntity<List<BillResponse>>
            getBillsByPatient(

                    @Parameter(
                            description = "Unique ID of the patient",
                            required = true,
                            example = "1"
                    )
                    @PathVariable Long patientId) {

        return ResponseEntity.ok(
                billService.getBillsByPatient(
                        patientId
                )
        );
    }

    // =========================================================
    // UPDATE BILL
    // =========================================================

    @PutMapping("/{id}")
    @Operation(
            summary = "Update bill",
            description = "Updates an existing billing record using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bill updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid bill data or validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bill or related patient not found"
            )
    })
    public ResponseEntity<BillResponse>
            updateBill(

                    @Parameter(
                            description = "Unique ID of the bill to update",
                            required = true,
                            example = "1"
                    )
                    @PathVariable Long id,

                    @Valid
                    @RequestBody
                    BillRequest request) {

        return ResponseEntity.ok(
                billService.updateBill(
                        id,
                        request
                )
        );
    }

    // =========================================================
    // DELETE BILL
    // =========================================================

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete bill",
            description = "Deletes an existing bill using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Bill deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid bill ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bill not found"
            )
    })
    public ResponseEntity<Void> deleteBill(

            @Parameter(
                    description = "Unique ID of the bill to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        billService.deleteBill(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // PAGINATION + SORTING
    // =========================================================

    @GetMapping("/page")
    @Operation(
            summary = "Get bills with pagination and sorting",
            description = "Retrieves bills using pagination and sorting. "
                    + "Default page is 0, default page size is 5, "
                    + "and default sorting is by ID in ascending order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bills retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    public ResponseEntity<PageResponse<BillResponse>>
    getBillsWithPaginationAndSorting(

            @Parameter(
                    description = "Page number, starting from 0",
                    example = "0",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    description = "Number of bills per page",
                    example = "5",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    description = "Field used for sorting. "
                            + "Allowed values: id, billDate, totalAmount, "
                            + "paidAmount, paymentStatus, paymentMethod, description",
                    example = "billDate",
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
                billService.getAllBills(
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
            summary = "Search and filter bills",
            description = "Searches bills using a keyword and supports filtering "
                    + "by payment status, payment method and bill date. "
                    + "Pagination and sorting are also supported."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bill search completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search, filter, pagination or sorting parameters"
            )
    })
    public ResponseEntity<PageResponse<BillResponse>>
    searchBills(

            @Parameter(
                    description = "Search keyword applied to bill description "
                            + "and patient-related information",
                    example = "consultation",
                    in = ParameterIn.QUERY
            )
            @RequestParam(required = false)
            String keyword,

            @Parameter(
                    description = "Filter bills by payment status",
                    example = "PAID",
                    in = ParameterIn.QUERY
            )
            @RequestParam(required = false)
            PaymentStatus paymentStatus,

            @Parameter(
                    description = "Filter bills by payment method",
                    example = "UPI",
                    in = ParameterIn.QUERY
            )
            @RequestParam(required = false)
            PaymentMethod paymentMethod,

            @Parameter(
                    description = "Filter bills by bill date",
                    example = "2026-09-14",
                    in = ParameterIn.QUERY
            )
            @RequestParam(required = false)
            LocalDate billDate,

            @Parameter(
                    description = "Page number, starting from 0",
                    example = "0",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    description = "Number of bills per page",
                    example = "5",
                    in = ParameterIn.QUERY
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    description = "Field used for sorting. "
                            + "Allowed values: id, billDate, totalAmount, "
                            + "paidAmount, paymentStatus, paymentMethod, description",
                    example = "totalAmount",
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
                billService.searchBills(
                        keyword,
                        paymentStatus,
                        paymentMethod,
                        billDate,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }
}