package com.clinica.dto;

import com.clinica.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

public class PrescriptionDtos {

    public record PrescriptionItemRequest(
        @NotBlank String medicationName,
        @NotBlank String dosage,
        @NotBlank String frequency,
        String duration,
        String instructions,
        @NotNull @Min(1) Integer quantity
    ) {}

    public record CreatePrescriptionRequest(
        @NotNull UUID consultationId,
        LocalDate expirationDate,
        String notes,
        @NotEmpty List<PrescriptionItemRequest> items
    ) {}

    public record PrescriptionItemResponse(
        UUID id,
        String medicationName,
        String dosage,
        String frequency,
        String duration,
        String instructions,
        Integer quantity
    ) {}

    public record PrescriptionResponse(
        UUID id,
        LocalDate issueDate,
        LocalDate expirationDate,
        String prescriptionNumber,
        String notes,
        List<PrescriptionItemResponse> items,
        LocalDateTime createdAt
    ) {}
}
