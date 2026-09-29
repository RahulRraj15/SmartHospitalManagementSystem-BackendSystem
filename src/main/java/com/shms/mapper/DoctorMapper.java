package com.shms.mapper;

import com.shms.dto.request.DoctorRequest;
import com.shms.dto.response.DoctorResponse;
import com.shms.entity.Department;
import com.shms.entity.Doctor;

public class DoctorMapper {

    private DoctorMapper() {
    }

    public static Doctor toEntity(
            DoctorRequest request,
            Department department) {

        return Doctor.builder()
                .specialization(request.getSpecialization())
                .qualification(request.getQualification())
                .experience(request.getExperience())
                .bio(request.getBio())
                .active(true)
                .department(department)
                .build();
    }

    public static DoctorResponse toResponse(Doctor doctor) {

        return DoctorResponse.builder()
                .id(doctor.getId())
                .createdAt(doctor.getCreatedAt())
                .updatedAt(doctor.getUpdatedAt())
                .specialization(doctor.getSpecialization())
                .qualification(doctor.getQualification())
                .experience(doctor.getExperience())
                .bio(doctor.getBio())
                .active(doctor.isActive())
                .departmentId(
                        doctor.getDepartment().getId()
                )
                .departmentName(
                        doctor.getDepartment().getName()
                )
                .build();
    }
}