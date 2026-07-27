package com.clinica.dto;

import com.clinica.entity.Patient;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DoctorDtos {

    public record CreateDoctorRequest(
        @NotBlank @Size(max = 30) String licenseNumber,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @Email String email,
        String phone,
        String biography,
        @NotNull @Min(10) Integer consultationFeeMinutes,
        @NotNull UUID specialtyId,
        UUID userId
    ) {}

    public record UpdateDoctorRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email String email,
        String phone,
        String biography,
        @Min(10) Integer consultationFeeMinutes
    ) {}

    public record DoctorResponse(
        UUID id,
        String licenseNumber,
        String firstName,
        String lastName,
        String email,
        String phone,
        String biography,
        Integer consultationFeeMinutes,
        Boolean active,
        SpecialtyDtos.SpecialtyResponse specialty
    ) {}

    public record DoctorSummaryResponse(
        UUID id,
        String fullName,
        String specialtyName,
        Boolean active
    ) {}
}
