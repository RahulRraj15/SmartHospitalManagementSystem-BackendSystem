package com.shms.service.implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shms.dto.request.MedicalReportRequest;
import com.shms.dto.response.MedicalReportResponse;
import com.shms.dto.response.PageResponse;
import com.shms.entity.Doctor;
import com.shms.entity.MedicalReport;
import com.shms.entity.Patient;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.MedicalReportMapper;
import com.shms.repository.DoctorRepository;
import com.shms.repository.MedicalReportRepository;
import com.shms.repository.PatientRepository;
import com.shms.service.MedicalReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalReportServiceImpl
        implements MedicalReportService {

    private final MedicalReportRepository medicalReportRepository;

    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;

    private final MedicalReportMapper medicalReportMapper;

    @Override
    public MedicalReportResponse createMedicalReport(
            MedicalReportRequest request) {

        Patient patient = patientRepository
                .findById(request.getPatientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with id: "
                                        + request.getPatientId()));

        Doctor doctor = doctorRepository
                .findById(request.getDoctorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with id: "
                                        + request.getDoctorId()));

        MedicalReport report =
                medicalReportMapper.toEntity(
                        request,
                        patient,
                        doctor);

        MedicalReport savedReport =
                medicalReportRepository.save(report);

        return medicalReportMapper.toResponse(savedReport);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalReportResponse getMedicalReportById(
            Long id) {

        MedicalReport report =
                medicalReportRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Medical report not found with id: "
                                                + id));

        return medicalReportMapper.toResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalReportResponse> getAllMedicalReports() {

        return medicalReportRepository.findAll()
                .stream()
                .map(medicalReportMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalReportResponse>
    getMedicalReportsByPatient(Long patientId) {

        if (!patientRepository.existsById(patientId)) {

            throw new ResourceNotFoundException(
                    "Patient not found with id: " + patientId);
        }

        return medicalReportRepository
                .findByPatientId(patientId)
                .stream()
                .map(medicalReportMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalReportResponse>
    getMedicalReportsByDoctor(Long doctorId) {

        if (!doctorRepository.existsById(doctorId)) {

            throw new ResourceNotFoundException(
                    "Doctor not found with id: " + doctorId);
        }

        return medicalReportRepository
                .findByDoctorId(doctorId)
                .stream()
                .map(medicalReportMapper::toResponse)
                .toList();
    }

    @Override
    public MedicalReportResponse updateMedicalReport(
            Long id,
            MedicalReportRequest request) {

        MedicalReport report =
                medicalReportRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Medical report not found with id: "
                                                + id));

        Patient patient = patientRepository
                .findById(request.getPatientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with id: "
                                        + request.getPatientId()));

        Doctor doctor = doctorRepository
                .findById(request.getDoctorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with id: "
                                        + request.getDoctorId()));

        medicalReportMapper.updateEntity(
                report,
                request);

        report.setPatient(patient);

        report.setDoctor(doctor);

        MedicalReport updatedReport =
                medicalReportRepository.save(report);

        return medicalReportMapper.toResponse(updatedReport);
    }

    @Override
    public void deleteMedicalReport(Long id) {

        MedicalReport report =
                medicalReportRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Medical report not found with id: "
                                                + id));

        medicalReportRepository.delete(report);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MedicalReportResponse>
    getAllMedicalReports(
            int page,
            int size,
            String sortBy,
            String direction) {

        if (page < 0) {

            throw new BadRequestException(
                    "Page number cannot be negative"
            );
        }

        if (size <= 0) {

            throw new BadRequestException(
                    "Page size must be greater than 0"
            );
        }

        if (size > 50) {

            throw new BadRequestException(
                    "Page size cannot be greater than 50"
            );
        }

        /*
         * Fields allowed for Medical Report sorting.
         */
        List<String> allowedSortFields = List.of(
                "id",
                "reportDate",
                "diagnosis",
                "symptoms",
                "treatment",
                "observations",
                "reportFileUrl"
        );

        if (!allowedSortFields.contains(sortBy)) {

            throw new BadRequestException(
                    "Invalid sort field: " + sortBy
            );
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new BadRequestException(
                    "Sort direction must be either 'asc' or 'desc'"
            );
        }

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );

        Page<MedicalReport> reportPage =
                medicalReportRepository.findAll(pageable);

        List<MedicalReportResponse> responses =
                reportPage.getContent()
                        .stream()
                        .map(medicalReportMapper::toResponse)
                        .toList();

        return new PageResponse<>(
                responses,
                reportPage.getNumber(),
                reportPage.getSize(),
                reportPage.getTotalElements(),
                reportPage.getTotalPages(),
                reportPage.isFirst(),
                reportPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MedicalReportResponse>
    searchMedicalReports(
            String keyword,
            LocalDate reportDate,
            Long patientId,
            Long doctorId,
            int page,
            int size,
            String sortBy,
            String direction) {

        if (page < 0) {

            throw new BadRequestException(
                    "Page number cannot be negative"
            );
        }

        if (size <= 0) {

            throw new BadRequestException(
                    "Page size must be greater than 0"
            );
        }

        if (size > 50) {

            throw new BadRequestException(
                    "Page size cannot be greater than 50"
            );
        }

        /*
         * Convert blank keyword to null.
         */
        if (keyword != null
                && keyword.trim().isEmpty()) {

            keyword = null;

        } else if (keyword != null) {

            keyword = keyword.trim();
        }

        /*
         * Fields allowed for Medical Report sorting.
         */
        List<String> allowedSortFields = List.of(
                "id",
                "reportDate",
                "diagnosis",
                "symptoms",
                "treatment",
                "observations",
                "reportFileUrl"
        );

        if (!allowedSortFields.contains(sortBy)) {

            throw new BadRequestException(
                    "Invalid sort field: " + sortBy
            );
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new BadRequestException(
                    "Sort direction must be either 'asc' or 'desc'"
            );
        }

        Sort sort;

        if (direction.equalsIgnoreCase("desc")) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );

        Page<MedicalReport> reportPage =
                medicalReportRepository.searchMedicalReports(
                        keyword,
                        reportDate,
                        patientId,
                        doctorId,
                        pageable
                );

        List<MedicalReportResponse> responses =
                reportPage.getContent()
                        .stream()
                        .map(medicalReportMapper::toResponse)
                        .toList();

        return new PageResponse<>(
                responses,
                reportPage.getNumber(),
                reportPage.getSize(),
                reportPage.getTotalElements(),
                reportPage.getTotalPages(),
                reportPage.isFirst(),
                reportPage.isLast()
        );
    }
}