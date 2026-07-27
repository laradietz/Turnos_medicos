package com.clinica.dto;

import com.clinica.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

public class ConsultationDtos {

    public record CreateConsultationRequest(
        @NotNull UUID appointmentId,
        String reason,
        String symptoms,
        String diagnosis,
        String treatment,
        String observations,
        BigDecimal weightKg,
        BigDecimal heightCm,
        String bloodPressure,
        BigDecimal temperature,
        Integer heartRate
    ) {}

    public record ConsultationResponse(
        UUID id,
        String reason,
        String symptoms,
        String diagnosis,
        String treatment,
        String observations,
        BigDecimal weightKg,
        BigDecimal heightCm,
        String bloodPressure,
        BigDecimal temperature,
        Integer heartRate,
        UUID appointmentId,
        LocalDateTime createdAt
    ) {}
}
