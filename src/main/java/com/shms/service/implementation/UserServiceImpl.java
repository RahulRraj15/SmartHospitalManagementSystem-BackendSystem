package com.shms.service.implementation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shms.dto.request.UserRequest;
import com.shms.dto.response.PageResponse;
import com.shms.dto.response.UserResponse;
import com.shms.entity.User;
import com.shms.entity.enums.Role;
import com.shms.entity.enums.UserStatus;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.UserMapper;
import com.shms.repository.UserRepository;
import com.shms.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // CREATE USER
    // =========================================================

    @Override
    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new BadRequestException(
                    "Email already exists: " + request.getEmail()
            );
        }

        if (request.getPhone() != null
                && userRepository.existsByPhone(request.getPhone())) {

            throw new BadRequestException(
                    "Phone already exists: " + request.getPhone()
            );
        }

        // DTO → Entity
        User user = UserMapper.toEntity(request);

        // Plain password → BCrypt password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Save entity
        User savedUser = userRepository.save(user);

        // Entity → Response DTO
        return UserMapper.toResponse(savedUser);
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );

        return UserMapper.toResponse(user);
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    // =========================================================
    // UPDATE USER
    // =========================================================

    @Override
    public UserResponse updateUser(
            Long id,
            UserRequest request) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );

        if (!existingUser.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new BadRequestException(
                    "Email already exists: " + request.getEmail()
            );
        }

        if (request.getPhone() != null
                && !request.getPhone().equals(existingUser.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {

            throw new BadRequestException(
                    "Phone already exists: " + request.getPhone()
            );
        }

        existingUser.setFirstName(
                request.getFirstName()
        );

        existingUser.setLastName(
                request.getLastName()
        );

        existingUser.setEmail(
                request.getEmail()
        );

        existingUser.setPhone(
                request.getPhone()
        );

        existingUser.setRole(
                request.getRole()
        );

        /*
         * Password is intentionally not updated here.
         * Password changes will be handled separately
         * using BCrypt.
         */

        User updatedUser =
                userRepository.save(existingUser);

        return UserMapper.toResponse(updatedUser);
    }

    // =========================================================
    // DELETE USER
    // =========================================================

    @Override
    public void deleteUser(Long id) {

        User existingUser =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: " + id
                                )
                        );

        userRepository.delete(existingUser);
    }

    // =========================================================
    // PAGINATION + SORTING
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(
            int page,
            int size,
            String sortBy,
            String direction) {

        // -----------------------------------------------------
        // Validate page number
        // -----------------------------------------------------

        if (page < 0) {

            throw new BadRequestException(
                    "Page number cannot be negative"
            );
        }

        // -----------------------------------------------------
        // Validate page size
        // -----------------------------------------------------

        if (size <= 0) {

            throw new BadRequestException(
                    "Page size must be greater than 0"
            );
        }

        // -----------------------------------------------------
        // Prevent very large requests
        // -----------------------------------------------------

        if (size > 50) {

            throw new BadRequestException(
                    "Page size cannot be greater than 50"
            );
        }

        // -----------------------------------------------------
        // Allowed sorting fields
        // -----------------------------------------------------

        List<String> allowedSortFields = List.of(
                "id",
                "firstName",
                "lastName",
                "email",
                "phone",
                "role",
                "status"
        );

        // -----------------------------------------------------
        // Validate sort field
        // -----------------------------------------------------

        if (!allowedSortFields.contains(sortBy)) {

            throw new BadRequestException(
                    "Invalid sort field: " + sortBy
            );
        }

        // -----------------------------------------------------
        // Validate sort direction
        // -----------------------------------------------------

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new BadRequestException(
                    "Sort direction must be either 'asc' or 'desc'"
            );
        }

        // -----------------------------------------------------
        // Create Sort object
        // -----------------------------------------------------

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        // -----------------------------------------------------
        // Create Pageable
        // -----------------------------------------------------

        Pageable pageable =
                PageRequest.of(page, size, sort);

        // -----------------------------------------------------
        // Fetch users
        // -----------------------------------------------------

        Page<User> userPage =
                userRepository.findAll(pageable);

        // -----------------------------------------------------
        // Entity → Response DTO
        // -----------------------------------------------------

        List<UserResponse> userResponses =
                userPage.getContent()
                        .stream()
                        .map(UserMapper::toResponse)
                        .toList();

        // -----------------------------------------------------
        // Return PageResponse
        // -----------------------------------------------------

        return new PageResponse<>(
                userResponses,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast()
        );
    }

    // =========================================================
    // SEARCH + FILTERING + PAGINATION + SORTING
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> searchUsers(
            String keyword,
            Role role,
            UserStatus status,
            int page,
            int size,
            String sortBy,
            String direction) {

        // -----------------------------------------------------
        // Validate page number
        // -----------------------------------------------------

        if (page < 0) {

            throw new BadRequestException(
                    "Page number cannot be negative"
            );
        }

        // -----------------------------------------------------
        // Validate page size
        // -----------------------------------------------------

        if (size <= 0) {

            throw new BadRequestException(
                    "Page size must be greater than 0"
            );
        }

        // -----------------------------------------------------
        // Prevent very large requests
        // -----------------------------------------------------

        if (size > 50) {

            throw new BadRequestException(
                    "Page size cannot be greater than 50"
            );
        }

        // -----------------------------------------------------
        // Allowed sorting fields
        // -----------------------------------------------------

        List<String> allowedSortFields = List.of(
                "id",
                "firstName",
                "lastName",
                "email",
                "phone",
                "role",
                "status"
        );

        // -----------------------------------------------------
        // Validate sort field
        // -----------------------------------------------------

        if (!allowedSortFields.contains(sortBy)) {

            throw new BadRequestException(
                    "Invalid sort field: " + sortBy
            );
        }

        // -----------------------------------------------------
        // Validate sort direction
        // -----------------------------------------------------

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new BadRequestException(
                    "Sort direction must be either 'asc' or 'desc'"
            );
        }

        // -----------------------------------------------------
        // Clean keyword
        // -----------------------------------------------------

        if (keyword != null) {

            keyword = keyword.trim();

            if (keyword.isBlank()) {
                keyword = null;
            }
        }

        // -----------------------------------------------------
        // Create Sort object
        // -----------------------------------------------------

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        // -----------------------------------------------------
        // Create Pageable
        // -----------------------------------------------------

        Pageable pageable =
                PageRequest.of(page, size, sort);

        // -----------------------------------------------------
        // Search database
        // -----------------------------------------------------

        Page<User> userPage =
                userRepository.searchUsers(
                        keyword,
                        role,
                        status,
                        pageable
                );

        // -----------------------------------------------------
        // Entity → Response DTO
        // -----------------------------------------------------

        List<UserResponse> userResponses =
                userPage.getContent()
                        .stream()
                        .map(UserMapper::toResponse)
                        .toList();

        // -----------------------------------------------------
        // Return PageResponse
        // -----------------------------------------------------

        return new PageResponse<>(
                userResponses,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast()
        );
    }
}