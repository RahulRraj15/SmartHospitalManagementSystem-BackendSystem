package com.shms.service.implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shms.dto.request.MedicineRequest;
import com.shms.dto.response.MedicineResponse;
import com.shms.dto.response.PageResponse;
import com.shms.entity.Medicine;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.MedicineMapper;
import com.shms.repository.MedicineRepository;
import com.shms.service.MedicineService;

@Service
@Transactional
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineServiceImpl(
            MedicineRepository medicineRepository) {

        this.medicineRepository = medicineRepository;
    }

    @Override
    public MedicineResponse createMedicine(
            MedicineRequest request) {

        if (medicineRepository.existsByNameIgnoreCase(
                request.getName())) {

            throw new BadRequestException(
                    "Medicine already exists with name: "
                            + request.getName()
            );
        }

        Medicine medicine =
                MedicineMapper.toEntity(request);

        Medicine savedMedicine =
                medicineRepository.save(medicine);

        return MedicineMapper.toResponse(savedMedicine);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicineResponse getMedicineById(
            Long id) {

        Medicine medicine =
                medicineRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Medicine not found with id: "
                                                + id
                                )
                        );

        return MedicineMapper.toResponse(medicine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicineResponse> getAllMedicines() {

        return medicineRepository.findAll()
                .stream()
                .map(MedicineMapper::toResponse)
                .toList();
    }

    @Override
    public MedicineResponse updateMedicine(
            Long id,
            MedicineRequest request) {

        Medicine existingMedicine =
                medicineRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Medicine not found with id: "
                                                + id
                                )
                        );

        boolean nameChanged =
                !existingMedicine.getName()
                        .equalsIgnoreCase(request.getName());

        if (nameChanged &&
                medicineRepository.existsByNameIgnoreCase(
                        request.getName())) {

            throw new BadRequestException(
                    "Medicine already exists with name: "
                            + request.getName()
            );
        }

        existingMedicine.setName(
                request.getName()
        );

        existingMedicine.setGenericName(
                request.getGenericName()
        );

        existingMedicine.setManufacturer(
                request.getManufacturer()
        );

        existingMedicine.setCategory(
                request.getCategory()
        );

        existingMedicine.setPrice(
                request.getPrice()
        );

        existingMedicine.setStockQuantity(
                request.getStockQuantity()
        );

        existingMedicine.setMinimumStockLevel(
                request.getMinimumStockLevel()
        );

        existingMedicine.setExpiryDate(
                request.getExpiryDate()
        );

        Medicine updatedMedicine =
                medicineRepository.save(existingMedicine);

        return MedicineMapper.toResponse(
                updatedMedicine
        );
    }

    @Override
    public void deleteMedicine(Long id) {

        Medicine medicine =
                medicineRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Medicine not found with id: "
                                                + id
                                )
                        );

        medicineRepository.delete(medicine);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MedicineResponse> getAllMedicines(
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
                "name",
                "genericName",
                "manufacturer",
                "category",
                "price",
                "stockQuantity",
                "minimumStockLevel",
                "expiryDate",
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

        // Fetch sorted and paginated medicines
        Page<Medicine> medicinePage =
                medicineRepository.findAll(pageable);

        // Convert entities to response DTOs
        List<MedicineResponse> medicineResponses =
                medicinePage.getContent()
                        .stream()
                        .map(MedicineMapper::toResponse)
                        .toList();

        // Return pagination response
        return new PageResponse<>(
                medicineResponses,
                medicinePage.getNumber(),
                medicinePage.getSize(),
                medicinePage.getTotalElements(),
                medicinePage.getTotalPages(),
                medicinePage.isFirst(),
                medicinePage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MedicineResponse> searchMedicines(
            String keyword,
            Boolean active,
            LocalDate expiryDate,
            Boolean stockAvailable,
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
                "name",
                "genericName",
                "manufacturer",
                "category",
                "price",
                "stockQuantity",
                "minimumStockLevel",
                "expiryDate",
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
        Page<Medicine> medicinePage =
                medicineRepository.searchMedicines(
                        keyword,
                        active,
                        expiryDate,
                        stockAvailable,
                        pageable
                );

        // Convert entities to response DTOs
        List<MedicineResponse> medicineResponses =
                medicinePage.getContent()
                        .stream()
                        .map(MedicineMapper::toResponse)
                        .toList();

        // Return pagination response
        return new PageResponse<>(
                medicineResponses,
                medicinePage.getNumber(),
                medicinePage.getSize(),
                medicinePage.getTotalElements(),
                medicinePage.getTotalPages(),
                medicinePage.isFirst(),
                medicinePage.isLast()
        );
    }
}