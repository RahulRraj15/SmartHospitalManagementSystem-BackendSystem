package com.shms.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.shms.entity.enums.BloodGroup;
import com.shms.entity.enums.Gender;

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
public class PatientResponse {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDate dateOfBirth;

    private Gender gender;

    private BloodGroup bloodGroup;

    private String address;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;
}