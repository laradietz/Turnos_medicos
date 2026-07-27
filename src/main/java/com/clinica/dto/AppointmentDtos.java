package com.clinica.dto;

import com.clinica.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

public class AppointmentDtos {

    public record CreateAppointmentRequest(
        @NotNull UUID patientId,
        @NotNull UUID doctorId,
        UUID officeId,
        UUID healthInsuranceId,
        @NotNull @FutureOrPresent LocalDate appointmentDate,
        @NotNull LocalTime startTime,
        @NotNull Appointment.AppointmentType type,
        String notes
    ) {}

    public record UpdateAppointmentRequest(
        LocalDate appointmentDate,
        LocalTime startTime,
        Appointment.AppointmentType type,
        String notes
    ) {}

    public record CancelAppointmentRequest(
        @NotBlank String cancellationReason
    ) {}

    public record AppointmentResponse(
        UUID id,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        String type,
        String notes,
        String cancellationReason,
        PatientDtos.PatientResponse patient,
        DoctorDtos.DoctorSummaryResponse doctor,
        String officeNumber,
        LocalDateTime createdAt
    ) {}

    public record AppointmentSummaryResponse(
        UUID id,
        LocalDate appointmentDate,
        LocalTime startTime,
        String status,
        String patientFullName,
        String doctorFullName,
        String type
    ) {}
}
