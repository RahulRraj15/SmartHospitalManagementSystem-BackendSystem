package com.shms.dto.request;

import java.time.LocalDate;

import com.shms.entity.enums.BloodGroup;
import com.shms.entity.enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRequest {

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @Size(
        max = 500,
        message = "Address cannot exceed 500 characters"
    )
    private String address;

    @Size(
        max = 100,
        message = "Emergency contact name cannot exceed 100 characters"
    )
    private String emergencyContactName;

    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Emergency contact phone must contain exactly 10 digits"
    )
    private String emergencyContactPhone;

    @NotNull(message = "User ID is required")
    private Long userId;
}