package com.shms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionMedicineResponse {

    private Long id;

    private Long medicineId;

    private String medicineName;

    private Integer quantity;

    private String dosage;

    private String frequency;

    private String duration;

    private String instructions;
}