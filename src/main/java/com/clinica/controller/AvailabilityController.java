package com.clinica.controller;

import com.clinica.dto.AvailabilityDtos;
import com.clinica.entity.*;
import com.clinica.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// ── AVAILABILITY CONTROLLER ──────────────────────────────────
@RestController
@RequestMapping("/api/disponibilidad")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    /**
     * GET /api/disponibilidad/{doctorId}/slots?date=2024-06-01
     * Devuelve los turnos disponibles (libres) del médico en una fecha.
     */
    @GetMapping("/{doctorId}/slots")
    public ResponseEntity<List<AvailabilityDtos.TimeSlotResponse>> getSlots(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(availabilityService.getAvailableSlots(doctorId, date));
    }

    /**
     * GET /api/disponibilidad/{doctorId}?from=&to=
     * Devuelve bloqueos/vacaciones del médico en un rango.
     */
    @GetMapping("/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    public ResponseEntity<List<Availability>> findByRange(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(availabilityService.findByDoctorAndRange(doctorId, from, to));
    }

    /** POST /api/disponibilidad/{doctorId} — Registrar bloqueo o vacación */
    @PostMapping("/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Availability> create(
            @PathVariable UUID doctorId,
            @Valid @RequestBody AvailabilityDtos.CreateAvailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availabilityService.create(doctorId, request));
    }
}
