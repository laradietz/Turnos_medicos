package com.clinica.dto;

import com.clinica.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

public class AvailabilityDtos {

    public record CreateAvailabilityRequest(
        @NotNull LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        @NotNull Availability.AvailabilityType type,
        String reason
    ) {}

    public record AvailabilityResponse(
        UUID id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String type,
        String reason
    ) {}

    public record TimeSlotResponse(
        LocalTime startTime,
        LocalTime endTime,
        boolean available
    ) {}
}
