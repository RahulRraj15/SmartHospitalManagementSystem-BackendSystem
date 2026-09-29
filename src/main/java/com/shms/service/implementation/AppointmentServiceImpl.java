package com.shms.service.implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shms.dto.request.AppointmentRequest;
import com.shms.dto.response.AppointmentResponse;
import com.shms.dto.response.PageResponse;
import com.shms.entity.Appointment;
import com.shms.entity.Doctor;
import com.shms.entity.Patient;
import com.shms.entity.enums.AppointmentStatus;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.AppointmentMapper;
import com.shms.repository.AppointmentRepository;
import com.shms.repository.DoctorRepository;
import com.shms.repository.PatientRepository;
import com.shms.service.AppointmentService;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final DoctorRepository doctorRepository;

    private final PatientRepository patientRepository;

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository) {

        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public AppointmentResponse createAppointment(
            AppointmentRequest request) {

        Doctor doctor =
                doctorRepository.findById(request.getDoctorId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Doctor not found with id: "
                                                + request.getDoctorId()
                                )
                        );

        Patient patient =
                patientRepository.findById(request.getPatientId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with id: "
                                                + request.getPatientId()
                                )
                        );

        boolean doctorBusy =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                                request.getDoctorId(),
                                request.getAppointmentDate(),
                                request.getAppointmentTime()
                        );

        if (doctorBusy) {

            throw new BadRequestException(
                    "Doctor is already booked for the selected date and time"
            );
        }

        boolean patientBusy =
                appointmentRepository
                        .existsByPatientIdAndAppointmentDateAndAppointmentTime(
                                request.getPatientId(),
                                request.getAppointmentDate(),
                                request.getAppointmentTime()
                        );

        if (patientBusy) {

            throw new BadRequestException(
                    "Patient already has an appointment for the selected date and time"
            );
        }

        Appointment appointment =
                AppointmentMapper.toEntity(
                        request,
                        doctor,
                        patient
                );

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return AppointmentMapper.toResponse(savedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {

        Appointment appointment =
                appointmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment not found with id: "
                                                + id
                                )
                        );

        return AppointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {

        return appointmentRepository.findAll()
                .stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponse updateAppointment(
            Long id,
            AppointmentRequest request) {

        Appointment existingAppointment =
                appointmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment not found with id: "
                                                + id
                                )
                        );

        Doctor doctor =
                doctorRepository.findById(request.getDoctorId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Doctor not found with id: "
                                                + request.getDoctorId()
                                )
                        );

        Patient patient =
                patientRepository.findById(request.getPatientId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with id: "
                                                + request.getPatientId()
                                )
                        );

        existingAppointment.setAppointmentDate(
                request.getAppointmentDate()
        );

        existingAppointment.setAppointmentTime(
                request.getAppointmentTime()
        );

        existingAppointment.setReason(
                request.getReason()
        );

        existingAppointment.setNotes(
                request.getNotes()
        );

        existingAppointment.setDoctor(doctor);

        existingAppointment.setPatient(patient);

        Appointment updatedAppointment =
                appointmentRepository.save(existingAppointment);

        return AppointmentMapper.toResponse(updatedAppointment);
    }

    @Override
    public void deleteAppointment(Long id) {

        Appointment appointment =
                appointmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment not found with id: "
                                                + id
                                )
                        );

        appointmentRepository.delete(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAllAppointments(
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
                "appointmentDate",
                "appointmentTime",
                "reason",
                "notes",
                "status"
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

        // Fetch sorted and paginated appointments
        Page<Appointment> appointmentPage =
                appointmentRepository.findAll(pageable);

        // Convert entities to response DTOs
        List<AppointmentResponse> appointmentResponses =
                appointmentPage.getContent()
                        .stream()
                        .map(AppointmentMapper::toResponse)
                        .toList();

        // Return pagination response
        return new PageResponse<>(
                appointmentResponses,
                appointmentPage.getNumber(),
                appointmentPage.getSize(),
                appointmentPage.getTotalElements(),
                appointmentPage.getTotalPages(),
                appointmentPage.isFirst(),
                appointmentPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> searchAppointments(
            String keyword,
            AppointmentStatus status,
            LocalDate appointmentDate,
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
                "appointmentDate",
                "appointmentTime",
                "reason",
                "notes",
                "status"
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

        // Clean keyword
        if (keyword != null) {

            keyword = keyword.trim();

            // Empty keyword means no keyword filter
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
        Page<Appointment> appointmentPage =
                appointmentRepository.searchAppointments(
                        keyword,
                        status,
                        appointmentDate,
                        pageable
                );

        // Convert entities to response DTOs
        List<AppointmentResponse> appointmentResponses =
                appointmentPage.getContent()
                        .stream()
                        .map(AppointmentMapper::toResponse)
                        .toList();

        // Return pagination response
        return new PageResponse<>(
                appointmentResponses,
                appointmentPage.getNumber(),
                appointmentPage.getSize(),
                appointmentPage.getTotalElements(),
                appointmentPage.getTotalPages(),
                appointmentPage.isFirst(),
                appointmentPage.isLast()
        );
    }
}