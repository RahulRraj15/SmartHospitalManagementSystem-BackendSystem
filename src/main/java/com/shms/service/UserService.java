package com.shms.service;

import java.util.List;

import com.shms.dto.request.UserRequest;
import com.shms.dto.response.PageResponse;
import com.shms.dto.response.UserResponse;
import com.shms.entity.enums.Role;
import com.shms.entity.enums.UserStatus;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(
            Long id,
            UserRequest request
    );

    void deleteUser(Long id);

    // =========================================================
    // PAGINATION + SORTING
    // =========================================================

    PageResponse<UserResponse> getAllUsers(
            int page,
            int size,
            String sortBy,
            String direction
    );

    // =========================================================
    // SEARCH + FILTERING
    // =========================================================

    PageResponse<UserResponse> searchUsers(
            String keyword,
            Role role,
            UserStatus status,
            int page,
            int size,
            String sortBy,
            String direction
    );
}