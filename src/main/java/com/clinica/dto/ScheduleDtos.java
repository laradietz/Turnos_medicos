package com.clinica.dto;

import com.clinica.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

public class ScheduleDtos {

    public record CreateScheduleRequest(
        @NotNull DayOfWeek dayOfWeek,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotNull @Min(10) Integer slotDurationMinutes,
        @NotNull LocalDate validFrom,
        LocalDate validUntil,
        UUID officeId
    ) {}

    public record ScheduleResponse(
        UUID id,
        String dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Integer slotDurationMinutes,
        LocalDate validFrom,
        LocalDate validUntil,
        Boolean active
    ) {}
}
