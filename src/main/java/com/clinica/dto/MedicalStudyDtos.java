package com.clinica.dto;

import com.clinica.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

public class MedicalStudyDtos {

    public record CreateMedicalStudyRequest(
        @NotNull UUID consultationId,
        @NotBlank String name,
        String studyType,
        String notes
    ) {}

    public record UpdateStudyResultRequest(
        String result,
        String fileUrl,
        LocalDate resultDate
    ) {}

    public record MedicalStudyResponse(
        UUID id,
        String name,
        String studyType,
        LocalDate orderedDate,
        LocalDate resultDate,
        String status,
        String result,
        String fileUrl,
        String notes
    ) {}
}
