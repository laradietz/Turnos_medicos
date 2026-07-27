package com.clinica.dto;

import com.clinica.entity.Patient;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SpecialtyDtos {

    public record CreateSpecialtyRequest(
        @NotBlank @Size(max = 100) String name,
        String description
    ) {}

    public record SpecialtyResponse(
        UUID id,
        String name,
        String description,
        Boolean active
    ) {}
}
