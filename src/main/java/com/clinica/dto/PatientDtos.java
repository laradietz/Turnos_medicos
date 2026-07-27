package com.clinica.dto;

import com.clinica.entity.Patient;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PatientDtos {

    public record CreatePatientRequest(
        @NotBlank @Size(max = 20) String dni,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotNull @Past LocalDate birthDate,
        Patient.Gender gender,
        @Email String email,
        String phone,
        String address,
        String city,
        Patient.BloodType bloodType,
        String emergencyContactName,
        String emergencyContactPhone
    ) {}

    public record UpdatePatientRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email String email,
        String phone,
        String address,
        String city,
        Patient.BloodType bloodType,
        String emergencyContactName,
        String emergencyContactPhone
    ) {}

    public record PatientResponse(
        UUID id,
        String dni,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String gender,
        String email,
        String phone,
        String address,
        String city,
        String bloodType,
        Boolean active,
        LocalDateTime createdAt
    ) {}

    public record PatientInsuranceRequest(
        @NotNull UUID healthInsuranceId,
        @NotBlank String affiliateNumber,
        String plan,
        LocalDate expirationDate,
        Boolean isPrimary
    ) {}

    public record PatientInsuranceResponse(
        UUID id,
        String healthInsuranceName,
        String affiliateNumber,
        String plan,
        LocalDate expirationDate,
        Boolean isPrimary
    ) {}
}
