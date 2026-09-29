package com.shms.service.implementation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shms.dto.request.PatientRequest;
import com.shms.dto.response.PageResponse;
import com.shms.dto.response.PatientResponse;
import com.shms.entity.Patient;
import com.shms.entity.User;
import com.shms.entity.enums.BloodGroup;
import com.shms.entity.enums.Gender;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.PatientMapper;
import com.shms.repository.PatientRepository;
import com.shms.repository.UserRepository;
import com.shms.service.PatientService;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    private final UserRepository userRepository;

    public PatientServiceImpl(
            PatientRepository patientRepository,
            UserRepository userRepository) {

        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PatientResponse createPatient(
            PatientRequest request) {

        User user =
                userRepository.findById(request.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + request.getUserId()
                                )
                        );

        if (patientRepository.existsByUserId(
                request.getUserId())) {

            throw new BadRequestException(
                    "Patient profile already exists for user id: "
                            + request.getUserId()
            );
        }

        Patient patient =
                PatientMapper.toEntity(request, user);

        Patient savedPatient =
                patientRepository.save(patient);

        return PatientMapper.toResponse(savedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {

        Patient patient =
                patientRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with id: "
                                                + id
                                )
                        );

        return PatientMapper.toResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {

        return patientRepository.findAll()
                .stream()
                .map(PatientMapper::toResponse)
                .toList();
    }

    @Override
    public PatientResponse updatePatient(
            Long id,
            PatientRequest request) {

        Patient existingPatient =
                patientRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with id: "
                                                + id
                                )
                        );

        User user =
                userRepository.findById(request.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + request.getUserId()
                                )
                        );

        if (!existingPatient.getUser().getId()
                .equals(request.getUserId())
                && patientRepository.existsByUserId(
                        request.getUserId())) {

            throw new BadRequestException(
                    "Patient profile already exists for user id: "
                            + request.getUserId()
            );
        }

        existingPatient.setDateOfBirth(
                request.getDateOfBirth()
        );

        existingPatient.setGender(
                request.getGender()
        );

        existingPatient.setBloodGroup(
                request.getBloodGroup()
        );

        existingPatient.setAddress(
                request.getAddress()
        );

        existingPatient.setEmergencyContactName(
                request.getEmergencyContactName()
        );

        existingPatient.setEmergencyContactPhone(
                request.getEmergencyContactPhone()
        );

        existingPatient.setUser(user);

        Patient updatedPatient =
                patientRepository.save(existingPatient);

        return PatientMapper.toResponse(updatedPatient);
    }

    @Override
    public void deletePatient(Long id) {

        Patient existingPatient =
                patientRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with id: "
                                                + id
                                )
                        );

        patientRepository.delete(existingPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PatientResponse> getAllPatients(
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

        // Prevent very large database requests
        if (size > 50) {

            throw new BadRequestException(
                    "Page size cannot be greater than 50"
            );
        }

        // Allowed fields for sorting
        List<String> allowedSortFields = List.of(
                "id",
                "dateOfBirth",
                "gender",
                "bloodGroup",
                "address",
                "emergencyContactName",
                "emergencyContactPhone"
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

        // Create sorting
        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        // Create pagination + sorting
        Pageable pageable =
                PageRequest.of(page, size, sort);

        // Fetch paginated and sorted patients
        Page<Patient> patientPage =
                patientRepository.findAll(pageable);

        // Convert entities to response DTOs
        List<PatientResponse> patientResponses =
                patientPage.getContent()
                        .stream()
                        .map(PatientMapper::toResponse)
                        .toList();

        // Return pagination response
        return new PageResponse<>(
                patientResponses,
                patientPage.getNumber(),
                patientPage.getSize(),
                patientPage.getTotalElements(),
                patientPage.getTotalPages(),
                patientPage.isFirst(),
                patientPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PatientResponse> searchPatients(
            String keyword,
            Gender gender,
            BloodGroup bloodGroup,
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

        // Prevent very large database requests
        if (size > 50) {

            throw new BadRequestException(
                    "Page size cannot be greater than 50"
            );
        }

        // Allowed fields for sorting
        List<String> allowedSortFields = List.of(
                "id",
                "dateOfBirth",
                "gender",
                "bloodGroup",
                "address",
                "emergencyContactName",
                "emergencyContactPhone"
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

        // Remove unnecessary spaces from keyword
        if (keyword != null) {

            keyword = keyword.trim();

            // Treat empty keyword as no keyword filter
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }

        // Create sorting
        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        // Create pagination + sorting
        Pageable pageable =
                PageRequest.of(page, size, sort);

        // Search + filtering
        Page<Patient> patientPage =
                patientRepository.searchPatients(
                        keyword,
                        gender,
                        bloodGroup,
                        pageable
                );

        // Convert entities to response DTOs
        List<PatientResponse> patientResponses =
                patientPage.getContent()
                        .stream()
                        .map(PatientMapper::toResponse)
                        .toList();

        // Return pagination response
        return new PageResponse<>(
                patientResponses,
                patientPage.getNumber(),
                patientPage.getSize(),
                patientPage.getTotalElements(),
                patientPage.getTotalPages(),
                patientPage.isFirst(),
                patientPage.isLast()
        );
    }
}