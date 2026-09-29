
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

import com.shms.dto.request.UserRequest;
import com.shms.dto.response.PageResponse;
import com.shms.dto.response.UserResponse;
import com.shms.entity.enums.Role;
import com.shms.entity.enums.UserStatus;
import com.shms.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "User Management",
        description = "APIs for creating, retrieving, updating, "
                + "searching and deleting hospital users"
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    // =========================================================
    // CREATE USER
    // =========================================================

    @Operation(
            summary = "Create a new user",
            description = "Creates a new hospital user and returns "
                    + "the created user details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = UserResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest request) {

        UserResponse response =
                userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Operation(
            summary = "Get all users",
            description = "Retrieves all users without pagination."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a single user using the user ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user ID"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the user",
                    required = true,
                    example = "1",
                    in = ParameterIn.PATH
            )
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }

    // =========================================================
    // PAGINATION + SORTING
    // =========================================================

    @Operation(
            summary = "Get users with pagination and sorting",
            description = "Retrieves users using pagination and sorting. "
                    + "Page numbering starts from 0."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    @GetMapping("/page")
    public ResponseEntity<PageResponse<UserResponse>>
    getUsersWithPaginationAndSorting(

            @Parameter(
                    name = "page",
                    description = "Page number. Starts from 0.",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    name = "size",
                    description = "Number of users per page. "
                            + "Maximum allowed size is 50.",
                    example = "5"
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    name = "sortBy",
                    description = "Field used for sorting. "
                            + "Example: id, firstName, email",
                    example = "id"
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
                userService.getAllUsers(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    // =========================================================
    // SEARCH + FILTERING
    // =========================================================

    @Operation(
            summary = "Search and filter users",
            description = "Searches users using a keyword and optionally "
                    + "filters them by role and status. Supports pagination "
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
    public ResponseEntity<PageResponse<UserResponse>>
    searchUsers(

            @Parameter(
                    name = "keyword",
                    description = "Search keyword applied to user "
                            + "name, email and phone",
                    example = "Rahul"
            )
            @RequestParam(required = false)
            String keyword,

            @Parameter(
                    name = "role",
                    description = "Filter users by role",
                    example = "DOCTOR"
            )
            @RequestParam(required = false)
            Role role,

            @Parameter(
                    name = "status",
                    description = "Filter users by account status",
                    example = "ACTIVE"
            )
            @RequestParam(required = false)
            UserStatus status,

            @Parameter(
                    name = "page",
                    description = "Page number. Starts from 0.",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    name = "size",
                    description = "Number of users per page. "
                            + "Maximum allowed size is 50.",
                    example = "5"
            )
            @RequestParam(defaultValue = "5")
            int size,

            @Parameter(
                    name = "sortBy",
                    description = "Field used for sorting",
                    example = "firstName"
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
                userService.searchUsers(
                        keyword,
                        role,
                        status,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    // =========================================================
    // UPDATE USER
    // =========================================================

    @Operation(
            summary = "Update an existing user",
            description = "Updates the details of an existing user "
                    + "using the user ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the user to update",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody UserRequest request) {

        return ResponseEntity.ok(
                userService.updateUser(id, request)
        );
    }

    // =========================================================
    // DELETE USER
    // =========================================================

    @Operation(
            summary = "Delete a user",
            description = "Deletes an existing user using the user ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user ID"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(

            @Parameter(
                    name = "id",
                    description = "Unique ID of the user to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}
