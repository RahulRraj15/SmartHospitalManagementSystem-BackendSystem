package com.shms.service.implementation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shms.dto.request.PrescriptionMedicineRequest;
import com.shms.dto.request.PrescriptionRequest;
import com.shms.dto.response.PageResponse;
import com.shms.dto.response.PrescriptionResponse;
import com.shms.entity.Appointment;
import com.shms.entity.Medicine;
import com.shms.entity.Prescription;
import com.shms.entity.PrescriptionMedicine;
import com.shms.exception.BadRequestException;
import com.shms.exception.MedicineOutOfStockException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.PrescriptionMapper;
import com.shms.repository.AppointmentRepository;
import com.shms.repository.MedicineRepository;
import com.shms.repository.PrescriptionRepository;
import com.shms.service.PrescriptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl
        implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    private final AppointmentRepository appointmentRepository;

    private final MedicineRepository medicineRepository;

    private final PrescriptionMapper prescriptionMapper;

    @Override
    public PrescriptionResponse createPrescription(
            PrescriptionRequest request) {

        Appointment appointment =
                appointmentRepository.findById(
                        request.getAppointmentId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found with id: "
                                        + request.getAppointmentId()
                        ));

        if (prescriptionRepository
                .existsByAppointmentId(appointment.getId())) {

            throw new BadRequestException(
                    "Prescription already exists for appointment id: "
                            + appointment.getId()
            );
        }

        Prescription prescription =
                Prescription.builder()
                        .prescriptionDate(
                                request.getPrescriptionDate())
                        .instructions(
                                request.getInstructions())
                        .appointment(appointment)
                        .medicines(new ArrayList<>())
                        .build();

        for (PrescriptionMedicineRequest medicineRequest
                : request.getMedicines()) {

            Medicine medicine =
                    medicineRepository.findById(
                            medicineRequest.getMedicineId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Medicine not found with id: "
                                            + medicineRequest
                                            .getMedicineId()
                            ));

            if (!medicine.isActive()) {

                throw new BadRequestException(
                        "Medicine is inactive: "
                                + medicine.getName()
                );
            }

            if (medicineRequest.getQuantity()
                    > medicine.getStockQuantity()) {

                throw new MedicineOutOfStockException(
                        "Insufficient stock for medicine: "
                                + medicine.getName()
                );
            }

            medicine.setStockQuantity(
                    medicine.getStockQuantity()
                            - medicineRequest.getQuantity()
            );

            PrescriptionMedicine prescriptionMedicine =
                    PrescriptionMedicine.builder()
                            .prescription(prescription)
                            .medicine(medicine)
                            .quantity(
                                    medicineRequest.getQuantity())
                            .dosage(
                                    medicineRequest.getDosage())
                            .frequency(
                                    medicineRequest.getFrequency())
                            .duration(
                                    medicineRequest.getDuration())
                            .instructions(
                                    medicineRequest.getInstructions())
                            .build();

            prescription.getMedicines()
                    .add(prescriptionMedicine);
        }

        Prescription savedPrescription =
                prescriptionRepository.save(prescription);

        return prescriptionMapper.toResponse(
                savedPrescription);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(
            Long id) {

        Prescription prescription =
                prescriptionRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Prescription not found with id: "
                                                + id
                                ));

        return prescriptionMapper.toResponse(
                prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse>
    getAllPrescriptions() {

        return prescriptionRepository.findAll()
                .stream()
                .map(prescriptionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse
    getPrescriptionByAppointment(
            Long appointmentId) {

        Prescription prescription =
                prescriptionRepository
                        .findByAppointmentId(appointmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Prescription not found for appointment id: "
                                                + appointmentId
                                ));

        return prescriptionMapper.toResponse(
                prescription);
    }

    @Override
    public PrescriptionResponse updatePrescription(
            Long id,
            PrescriptionRequest request) {

        Prescription prescription =
                prescriptionRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Prescription not found with id: "
                                                + id
                                ));

        Appointment appointment =
                appointmentRepository.findById(
                        request.getAppointmentId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found with id: "
                                        + request.getAppointmentId()
                        ));

        /*
         * Restore the old medicine stock before replacing
         * prescription medicines.
         */
        for (PrescriptionMedicine oldItem
                : prescription.getMedicines()) {

            Medicine oldMedicine = oldItem.getMedicine();

            oldMedicine.setStockQuantity(
                    oldMedicine.getStockQuantity()
                            + oldItem.getQuantity()
            );
        }

        prescription.getMedicines().clear();

        prescription.setPrescriptionDate(
                request.getPrescriptionDate());

        prescription.setInstructions(
                request.getInstructions());

        prescription.setAppointment(appointment);

        for (PrescriptionMedicineRequest medicineRequest
                : request.getMedicines()) {

            Medicine medicine =
                    medicineRepository.findById(
                            medicineRequest.getMedicineId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Medicine not found with id: "
                                            + medicineRequest
                                            .getMedicineId()
                            ));

            if (!medicine.isActive()) {

                throw new BadRequestException(
                        "Medicine is inactive: "
                                + medicine.getName()
                );
            }

            if (medicineRequest.getQuantity()
                    > medicine.getStockQuantity()) {

                throw new MedicineOutOfStockException(
                        "Insufficient stock for medicine: "
                                + medicine.getName()
                );
            }

            medicine.setStockQuantity(
                    medicine.getStockQuantity()
                            - medicineRequest.getQuantity()
            );

            PrescriptionMedicine item =
                    PrescriptionMedicine.builder()
                            .prescription(prescription)
                            .medicine(medicine)
                            .quantity(
                                    medicineRequest.getQuantity())
                            .dosage(
                                    medicineRequest.getDosage())
                            .frequency(
                                    medicineRequest.getFrequency())
                            .duration(
                                    medicineRequest.getDuration())
                            .instructions(
                                    medicineRequest.getInstructions())
                            .build();

            prescription.getMedicines().add(item);
        }

        Prescription updatedPrescription =
                prescriptionRepository.save(
                        prescription);

        return prescriptionMapper.toResponse(
                updatedPrescription);
    }

    @Override
    public void deletePrescription(Long id) {

        Prescription prescription =
                prescriptionRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Prescription not found with id: "
                                                + id
                                ));

        /*
         * Return medicines to stock before deleting
         * the prescription.
         */
        for (PrescriptionMedicine item
                : prescription.getMedicines()) {

            Medicine medicine = item.getMedicine();

            medicine.setStockQuantity(
                    medicine.getStockQuantity()
                            + item.getQuantity()
            );
        }

        prescriptionRepository.delete(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PrescriptionResponse>
    getAllPrescriptions(
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
         * Allowed fields for Prescription sorting.
         */
        List<String> allowedSortFields = List.of(
                "id",
                "prescriptionDate"
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

        Page<Prescription> prescriptionPage =
                prescriptionRepository.findAll(pageable);

        List<PrescriptionResponse> prescriptionResponses =
                prescriptionPage.getContent()
                        .stream()
                        .map(prescriptionMapper::toResponse)
                        .toList();

        return new PageResponse<>(
                prescriptionResponses,
                prescriptionPage.getNumber(),
                prescriptionPage.getSize(),
                prescriptionPage.getTotalElements(),
                prescriptionPage.getTotalPages(),
                prescriptionPage.isFirst(),
                prescriptionPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PrescriptionResponse>
    searchPrescriptions(
            String keyword,
            LocalDate prescriptionDate,
            Long appointmentId,
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
         *
         * This allows the repository query to behave
         * like "no keyword filter".
         */
        if (keyword != null
                && keyword.trim().isEmpty()) {

            keyword = null;

        } else if (keyword != null) {

            keyword = keyword.trim();
        }

        /*
         * Allowed fields for Prescription sorting.
         */
        List<String> allowedSortFields = List.of(
                "id",
                "prescriptionDate"
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

        Page<Prescription> prescriptionPage =
                prescriptionRepository.searchPrescriptions(
                        keyword,
                        prescriptionDate,
                        appointmentId,
                        pageable
                );

        List<PrescriptionResponse> prescriptionResponses =
                prescriptionPage.getContent()
                        .stream()
                        .map(prescriptionMapper::toResponse)
                        .toList();

        return new PageResponse<>(
                prescriptionResponses,
                prescriptionPage.getNumber(),
                prescriptionPage.getSize(),
                prescriptionPage.getTotalElements(),
                prescriptionPage.getTotalPages(),
                prescriptionPage.isFirst(),
                prescriptionPage.isLast()
        );
    }
}