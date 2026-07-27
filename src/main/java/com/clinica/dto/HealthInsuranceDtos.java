package com.clinica.dto;

import com.clinica.entity.Patient;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class HealthInsuranceDtos {

    public record CreateHealthInsuranceRequest(
        @NotBlank @Size(max = 150) String name,
        String code,
        String address,
        String phone
    ) {}

    public record HealthInsuranceResponse(
        UUID id,
        String name,
        String code,
        String phone,
        Boolean active
    ) {}
}
