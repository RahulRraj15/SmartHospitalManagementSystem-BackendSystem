package com.shms.service.implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shms.dto.request.BillRequest;
import com.shms.dto.response.BillResponse;
import com.shms.dto.response.PageResponse;
import com.shms.entity.Bill;
import com.shms.entity.Patient;
import com.shms.entity.enums.PaymentMethod;
import com.shms.entity.enums.PaymentStatus;
import com.shms.exception.BadRequestException;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.BillMapper;
import com.shms.repository.BillRepository;
import com.shms.repository.PatientRepository;
import com.shms.service.BillService;

@Service
@Transactional
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    private final PatientRepository patientRepository;

    public BillServiceImpl(
            BillRepository billRepository,
            PatientRepository patientRepository) {

        this.billRepository = billRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public BillResponse createBill(
            BillRequest request) {

        validatePaidAmount(
                request.getTotalAmount(),
                request.getPaidAmount()
        );

        Patient patient =
                patientRepository.findById(
                        request.getPatientId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with id: "
                                        + request.getPatientId()
                        )
                );

        Bill bill =
                BillMapper.toEntity(request, patient);

        updatePaymentStatus(bill);

        Bill savedBill =
                billRepository.save(bill);

        return BillMapper.toResponse(savedBill);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse getBillById(Long id) {

        Bill bill =
                billRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Bill not found with id: "
                                                + id
                                )
                        );

        return BillMapper.toResponse(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillResponse> getAllBills() {

        return billRepository.findAll()
                .stream()
                .map(BillMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillResponse> getBillsByPatient(
            Long patientId) {

        if (!patientRepository.existsById(patientId)) {

            throw new ResourceNotFoundException(
                    "Patient not found with id: "
                            + patientId
            );
        }

        return billRepository
                .findByPatientId(patientId)
                .stream()
                .map(BillMapper::toResponse)
                .toList();
    }

    @Override
    public BillResponse updateBill(
            Long id,
            BillRequest request) {

        validatePaidAmount(
                request.getTotalAmount(),
                request.getPaidAmount()
        );

        Bill existingBill =
                billRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Bill not found with id: "
                                                + id
                                )
                        );

        Patient patient =
                patientRepository.findById(
                        request.getPatientId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with id: "
                                        + request.getPatientId()
                        )
                );

        existingBill.setBillDate(
                request.getBillDate()
        );

        existingBill.setTotalAmount(
                request.getTotalAmount()
        );

        existingBill.setPaidAmount(
                request.getPaidAmount()
        );

        existingBill.setPaymentMethod(
                request.getPaymentMethod()
        );

        existingBill.setDescription(
                request.getDescription()
        );

        existingBill.setPatient(patient);

        updatePaymentStatus(existingBill);

        Bill updatedBill =
                billRepository.save(existingBill);

        return BillMapper.toResponse(updatedBill);
    }

    @Override
    public void deleteBill(Long id) {

        Bill bill =
                billRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Bill not found with id: "
                                                + id
                                )
                        );

        billRepository.delete(bill);
    }

    private void validatePaidAmount(
            BigDecimal totalAmount,
            BigDecimal paidAmount) {

        if (paidAmount.compareTo(totalAmount) > 0) {

            throw new BadRequestException(
                    "Paid amount cannot be greater than total amount"
            );
        }
    }

    private void updatePaymentStatus(
            Bill bill) {

        BigDecimal total =
                bill.getTotalAmount();

        BigDecimal paid =
                bill.getPaidAmount();

        if (paid.compareTo(BigDecimal.ZERO) == 0) {

            bill.setPaymentStatus(
                    PaymentStatus.PENDING
            );

        } else if (paid.compareTo(total) < 0) {

            bill.setPaymentStatus(
                    PaymentStatus.PARTIAL
            );

        } else {

            bill.setPaymentStatus(
                    PaymentStatus.PAID
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BillResponse> getAllBills(
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
         * Fields allowed for Bill sorting.
         */
        List<String> allowedSortFields = List.of(
                "id",
                "billDate",
                "totalAmount",
                "paidAmount",
                "paymentStatus",
                "paymentMethod",
                "description"
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

        Page<Bill> billPage =
                billRepository.findAll(pageable);

        List<BillResponse> billResponses =
                billPage.getContent()
                        .stream()
                        .map(BillMapper::toResponse)
                        .toList();

        return new PageResponse<>(
                billResponses,
                billPage.getNumber(),
                billPage.getSize(),
                billPage.getTotalElements(),
                billPage.getTotalPages(),
                billPage.isFirst(),
                billPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BillResponse> searchBills(
            String keyword,
            PaymentStatus paymentStatus,
            PaymentMethod paymentMethod,
            LocalDate billDate,
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
         * Fields allowed for Bill sorting.
         */
        List<String> allowedSortFields = List.of(
                "id",
                "billDate",
                "totalAmount",
                "paidAmount",
                "paymentStatus",
                "paymentMethod",
                "description"
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

        Page<Bill> billPage =
                billRepository.searchBills(
                        keyword,
                        paymentStatus,
                        paymentMethod,
                        billDate,
                        pageable
                );

        List<BillResponse> billResponses =
                billPage.getContent()
                        .stream()
                        .map(BillMapper::toResponse)
                        .toList();

        return new PageResponse<>(
                billResponses,
                billPage.getNumber(),
                billPage.getSize(),
                billPage.getTotalElements(),
                billPage.getTotalPages(),
                billPage.isFirst(),
                billPage.isLast()
        );
    }
}