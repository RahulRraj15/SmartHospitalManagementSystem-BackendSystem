package com.shms.service.implementation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shms.dto.request.DepartmentRequest;
import com.shms.dto.response.DepartmentResponse;
import com.shms.dto.response.PageResponse;
import com.shms.entity.Department;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.DepartmentMapper;
import com.shms.repository.DepartmentRepository;
import com.shms.service.DepartmentService;

@Service
@Transactional
public class DepartmentServiceImpl
        implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(
            DepartmentRepository departmentRepository) {

        this.departmentRepository = departmentRepository;
    }

    // =====================================================
    // CREATE DEPARTMENT
    // =====================================================

    @Override
    public DepartmentResponse createDepartment(
            DepartmentRequest request) {

        if (departmentRepository.existsByName(request.getName())) {

            throw new BadRequestException(
                    "Department already exists: "
                            + request.getName()
            );
        }

        Department department =
                DepartmentMapper.toEntity(request);

        Department savedDepartment =
                departmentRepository.save(department);

        return DepartmentMapper.toResponse(
                savedDepartment
        );
    }

    // =====================================================
    // GET DEPARTMENT BY ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {

        Department department =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Department not found with id: "
                                                + id
                                )
                        );

        return DepartmentMapper.toResponse(
                department
        );
    }

    // =====================================================
    // GET ALL DEPARTMENTS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {

        return departmentRepository.findAll()
                .stream()
                .map(DepartmentMapper::toResponse)
                .toList();
    }

    // =====================================================
    // UPDATE DEPARTMENT
    // =====================================================

    @Override
    public DepartmentResponse updateDepartment(
            Long id,
            DepartmentRequest request) {

        Department existingDepartment =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Department not found with id: "
                                                + id
                                )
                        );

        if (!existingDepartment.getName().equals(request.getName())
                && departmentRepository.existsByName(
                        request.getName())) {

            throw new BadRequestException(
                    "Department already exists: "
                            + request.getName()
            );
        }

        existingDepartment.setName(
                request.getName()
        );

        existingDepartment.setDescription(
                request.getDescription()
        );

        Department updatedDepartment =
                departmentRepository.save(
                        existingDepartment
                );

        return DepartmentMapper.toResponse(
                updatedDepartment
        );
    }

    // =====================================================
    // DELETE DEPARTMENT
    // =====================================================

    @Override
    public void deleteDepartment(Long id) {

        Department existingDepartment =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Department not found with id: "
                                                + id
                                )
                        );

        departmentRepository.delete(
                existingDepartment
        );
    }

    // =====================================================
    // PAGINATION + SORTING
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse>
    getAllDepartments(
            int page,
            int size,
            String sortBy,
            String direction) {

        // Validate page number

        if (page < 0) {

            throw new BadRequestException(
                    "Page number cannot be negative"
            );
        }

        // Validate page size

        if (size <= 0) {

            throw new BadRequestException(
                    "Page size must be greater than 0"
            );
        }

        // Prevent very large requests

        if (size > 50) {

            throw new BadRequestException(
                    "Page size cannot be greater than 50"
            );
        }

        // Allowed sorting fields

        List<String> allowedSortFields = List.of(
                "id",
                "name",
                "description",
                "active"
        );

        // Validate sort field

        if (!allowedSortFields.contains(sortBy)) {

            throw new BadRequestException(
                    "Invalid sort field: " + sortBy
            );
        }

        // Validate sort direction

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new BadRequestException(
                    "Sort direction must be either 'asc' or 'desc'"
            );
        }

        // Create Sort object

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        // Pagination + Sorting

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );

        // Fetch departments

        Page<Department> departmentPage =
                departmentRepository.findAll(
                        pageable
                );

        // Entity → Response DTO

        List<DepartmentResponse>
                departmentResponses =
                departmentPage.getContent()
                        .stream()
                        .map(DepartmentMapper::toResponse)
                        .toList();

        // Return pagination response

        return new PageResponse<>(
                departmentResponses,
                departmentPage.getNumber(),
                departmentPage.getSize(),
                departmentPage.getTotalElements(),
                departmentPage.getTotalPages(),
                departmentPage.isFirst(),
                departmentPage.isLast()
        );
    }

    // =====================================================
    // SEARCH + FILTERING + PAGINATION + SORTING
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse>
    searchDepartments(
            String keyword,
            Boolean active,
            int page,
            int size,
            String sortBy,
            String direction) {

        // -------------------------------------------------
        // Validate page number
        // -------------------------------------------------

        if (page < 0) {

            throw new BadRequestException(
                    "Page number cannot be negative"
            );
        }

        // -------------------------------------------------
        // Validate page size
        // -------------------------------------------------

        if (size <= 0) {

            throw new BadRequestException(
                    "Page size must be greater than 0"
            );
        }

        // -------------------------------------------------
        // Prevent very large requests
        // -------------------------------------------------

        if (size > 50) {

            throw new BadRequestException(
                    "Page size cannot be greater than 50"
            );
        }

        // -------------------------------------------------
        // Allowed sorting fields
        // -------------------------------------------------

        List<String> allowedSortFields = List.of(
                "id",
                "name",
                "description",
                "active"
        );

        // -------------------------------------------------
        // Validate sort field
        // -------------------------------------------------

        if (!allowedSortFields.contains(sortBy)) {

            throw new BadRequestException(
                    "Invalid sort field: " + sortBy
            );
        }

        // -------------------------------------------------
        // Validate sort direction
        // -------------------------------------------------

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new BadRequestException(
                    "Sort direction must be either 'asc' or 'desc'"
            );
        }

        // -------------------------------------------------
        // Clean keyword
        // -------------------------------------------------

        if (keyword != null) {

            keyword = keyword.trim();

            if (keyword.isBlank()) {

                keyword = null;
            }
        }

        // -------------------------------------------------
        // Create Sort object
        // -------------------------------------------------

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        // -------------------------------------------------
        // Create Pageable
        // -------------------------------------------------

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );

        // -------------------------------------------------
        // Search database
        // -------------------------------------------------

        Page<Department> departmentPage =
                departmentRepository.searchDepartments(
                        keyword,
                        active,
                        pageable
                );

        // -------------------------------------------------
        // Entity → Response DTO
        // -------------------------------------------------

        List<DepartmentResponse>
                departmentResponses =
                departmentPage.getContent()
                        .stream()
                        .map(DepartmentMapper::toResponse)
                        .toList();

        // -------------------------------------------------
        // Return PageResponse
        // -------------------------------------------------

        return new PageResponse<>(
                departmentResponses,
                departmentPage.getNumber(),
                departmentPage.getSize(),
                departmentPage.getTotalElements(),
                departmentPage.getTotalPages(),
                departmentPage.isFirst(),
                departmentPage.isLast()
        );
    }
}