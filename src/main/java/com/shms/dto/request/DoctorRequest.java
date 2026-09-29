package com.shms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class DoctorRequest {

    @NotBlank(message = "Specialization is required")
    @Size(
        min = 2,
        max = 100,
        message = "Specialization must be between 2 and 100 characters"
    )
    private String specialization;

    @Size(
        max = 100,
        message = "Qualification cannot exceed 100 characters"
    )
    private String qualification;

    @Size(
        max = 20,
        message = "Experience cannot exceed 20 characters"
    )
    private String experience;

    @Size(
        max = 500,
        message = "Bio cannot exceed 500 characters"
    )
    private String bio;

    @NotNull(message = "Department ID is required")
    private Long departmentId;
}