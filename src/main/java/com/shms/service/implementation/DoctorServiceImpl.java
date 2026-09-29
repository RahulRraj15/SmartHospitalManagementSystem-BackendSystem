package com.shms.service.implementation;

import com.shms.dto.request.DoctorRequest;
import com.shms.dto.response.DoctorResponse;
import com.shms.dto.response.PageResponse;
import com.shms.entity.Department;
import com.shms.entity.Doctor;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.DoctorMapper;
import com.shms.repository.DepartmentRepository;
import com.shms.repository.DoctorRepository;
import com.shms.service.DoctorService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    private final DepartmentRepository departmentRepository;

    // =====================================================
    // CREATE DOCTOR
    // =====================================================

    @Override
    @Transactional
    public DoctorResponse createDoctor(
            DoctorRequest request) {

        Department department =
                departmentRepository.findById(
                        request.getDepartmentId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id: "
                                        + request.getDepartmentId()
                        )
                );

        Doctor doctor =
                DoctorMapper.toEntity(
                        request,
                        department
                );

        Doctor savedDoctor =
                doctorRepository.save(doctor);

        return DoctorMapper.toResponse(
                savedDoctor
        );
    }

    // =====================================================
    // GET DOCTOR BY ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {

        Doctor doctor =
                doctorRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Doctor not found with id: "
                                                + id
                                )
                        );

        return DoctorMapper.toResponse(doctor);
    }

    // =====================================================
    // GET ALL DOCTORS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> getAllDoctors() {

        return doctorRepository.findAll()
                .stream()
                .map(DoctorMapper::toResponse)
                .toList();
    }

    // =====================================================
    // PAGINATION + SORTING
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DoctorResponse> getAllDoctors(
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
                "specialization",
                "qualification",
                "experience",
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

        // Create Pageable

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );

        // Fetch paginated and sorted doctors

        Page<Doctor> doctorPage =
                doctorRepository.findAll(pageable);

        // Entity → Response DTO

        List<DoctorResponse> doctorResponses =
                doctorPage.getContent()
                        .stream()
                        .map(DoctorMapper::toResponse)
                        .toList();

        // Return pagination response

        return new PageResponse<>(
                doctorResponses,
                doctorPage.getNumber(),
                doctorPage.getSize(),
                doctorPage.getTotalElements(),
                doctorPage.getTotalPages(),
                doctorPage.isFirst(),
                doctorPage.isLast()
        );
    }

    // =====================================================
    // SEARCH + FILTERING + PAGINATION + SORTING
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DoctorResponse> searchDoctors(
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
                "specialization",
                "qualification",
                "experience",
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

        Page<Doctor> doctorPage =
                doctorRepository.searchDoctors(
                        keyword,
                        active,
                        pageable
                );

        // -------------------------------------------------
        // Entity → Response DTO
        // -------------------------------------------------

        List<DoctorResponse> doctorResponses =
                doctorPage.getContent()
                        .stream()
                        .map(DoctorMapper::toResponse)
                        .toList();

        // -------------------------------------------------
        // Return PageResponse
        // -------------------------------------------------

        return new PageResponse<>(
                doctorResponses,
                doctorPage.getNumber(),
                doctorPage.getSize(),
                doctorPage.getTotalElements(),
                doctorPage.getTotalPages(),
                doctorPage.isFirst(),
                doctorPage.isLast()
        );
    }

    // =====================================================
    // UPDATE DOCTOR
    // =====================================================

    @Override
    @Transactional
    public DoctorResponse updateDoctor(
            Long id,
            DoctorRequest request) {

        Doctor doctor =
                doctorRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Doctor not found with id: "
                                                + id
                                )
                        );

        Department department =
                departmentRepository.findById(
                        request.getDepartmentId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id: "
                                        + request.getDepartmentId()
                        )
                );

        /*
         * Update the existing doctor fields manually
         * because the current DoctorMapper does not
         * contain an updateEntity() method.
         */

        doctor.setSpecialization(
                request.getSpecialization()
        );

        doctor.setQualification(
                request.getQualification()
        );

        doctor.setExperience(
                request.getExperience()
        );

        doctor.setBio(
                request.getBio()
        );

        doctor.setDepartment(department);

        Doctor updatedDoctor =
                doctorRepository.save(doctor);

        return DoctorMapper.toResponse(
                updatedDoctor
        );
    }

    // =====================================================
    // DELETE DOCTOR
    // =====================================================

    @Override
    @Transactional
    public void deleteDoctor(Long id) {

        Doctor doctor =
                doctorRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Doctor not found with id: "
                                                + id
                                )
                        );

        doctorRepository.delete(doctor);
    }
}