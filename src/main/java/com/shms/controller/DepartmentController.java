
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

import com.shms.dto.request.DepartmentRequest;
import com.shms.dto.response.DepartmentResponse;
import com.shms.dto.response.PageResponse;
import com.shms.service.DepartmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/departments")
@Tag(
        name = "Department Management",
        description = "APIs for creating, retrieving, updating, "
                + "searching and deleting hospital departments"
)
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(
            DepartmentService departmentService) {

        this.departmentService = departmentService;
    }

    // =====================================================
    // CREATE DEPARTMENT
    // =====================================================

    @Operation(
            summary = "Create a new department",
            description = "Creates a new hospital department and "
                    + "returns the created department details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Department created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid department data"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PostMapping
    public ResponseEntity<DepartmentResponse>
    createDepartment(
            @Valid @RequestBody DepartmentRequest request) {

        DepartmentResponse response =
                departmentService.createDepartment(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // GET ALL DEPARTMENTS
    // =====================================================

    @Operation(
            summary = "Get all departments",
            description = "Retrieves all hospital departments "
                    + "without pagination."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Departments retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<DepartmentResponse>>
    getAllDepartments() {

        return ResponseEntity.ok(
                departmentService.getAllDepartments()
        );
    }

    // =====================================================
    // GET DEPARTMENT BY ID
    // =====================================================

    @Operation(
            summary = "Get department by ID",
            description = "Retrieves a single department using "
                    + "the department ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Department found successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Department not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid department ID"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse>
    getDepartmentById(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the department",
                    required = true,
                    example = "1",
                    in = ParameterIn.PATH
            )
            @PathVariable Long id) {

        return ResponseEntity.ok(
                departmentService.getDepartmentById(id)
        );
    }

    // =====================================================
    // PAGINATION + SORTING
    // =====================================================

    @Operation(
            summary = "Get departments with pagination and sorting",
            description = "Retrieves departments using pagination "
                    + "and sorting. Page numbering starts from 0."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Departments retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    @GetMapping("/page")
    public ResponseEntity<PageResponse<DepartmentResponse>>
    getDepartmentsWithPaginationAndSorting(

            @Parameter(
                    name = "page",
                    description = "Page number. Starts from 0.",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    name = "size",
                    description = "Number of departments per page. "
                            + "Maximum allowed size is 50.",
                    example = "5"
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    name = "sortBy",
                    description = "Field used for sorting. "
                            + "Example: id, name, description",
                    example = "name"
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
                departmentService.getAllDepartments(
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
            summary = "Search and filter departments",
            description = "Searches departments using a keyword "
                    + "and optionally filters them by active status. "
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
    public ResponseEntity<PageResponse<DepartmentResponse>>
    searchDepartments(

            @Parameter(
                    name = "keyword",
                    description = "Search keyword applied to "
                            + "department name and description",
                    example = "Cardiology"
            )
            @RequestParam(required = false)
            String keyword,

            @Parameter(
                    name = "active",
                    description = "Filter departments by active status",
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
                    description = "Number of departments per page. "
                            + "Maximum allowed size is 50.",
                    example = "5"
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    name = "sortBy",
                    description = "Field used for sorting",
                    example = "name"
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
                departmentService.searchDepartments(
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
    // UPDATE DEPARTMENT
    // =====================================================

    @Operation(
            summary = "Update an existing department",
            description = "Updates an existing hospital department "
                    + "using the department ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Department updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid department data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Department not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse>
    updateDepartment(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the department to update",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody DepartmentRequest request) {

        return ResponseEntity.ok(
                departmentService.updateDepartment(
                        id,
                        request
                )
        );
    }

    // =====================================================
    // DELETE DEPARTMENT
    // =====================================================

    @Operation(
            summary = "Delete a department",
            description = "Deletes an existing department using "
                    + "the department ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Department deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Department not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid department ID"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the department to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        departmentService.deleteDepartment(id);

        return ResponseEntity.noContent().build();
    }
}
