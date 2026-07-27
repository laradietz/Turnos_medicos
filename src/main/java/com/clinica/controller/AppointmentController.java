package com.clinica.controller;

import com.clinica.dto.AppointmentDtos;
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

// ── APPOINTMENT CONTROLLER ───────────────────────────────────
@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /** GET /api/turnos?doctorId=&date=2024-06-01 */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    public ResponseEntity<List<Appointment>> findByDoctorAndDate(
            @RequestParam UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.findByDoctor(doctorId, date));
    }

    /**
     * GET /api/turnos/rango?doctorId=&from=&to=
     * Usado para pintar el calendario mensual (qué días tiene turnos el médico).
     */
    @GetMapping("/rango")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    public ResponseEntity<List<Appointment>> findByDoctorAndRange(
            @RequestParam UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(appointmentService.findByDoctorAndRange(doctorId, from, to));
    }

    /** GET /api/turnos/paciente/{patientId} */
    @GetMapping("/paciente/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    public ResponseEntity<List<Appointment>> findByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(appointmentService.findByPatient(patientId));
    }

    /** GET /api/turnos/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    public ResponseEntity<Appointment> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    /** POST /api/turnos — Solicitar turno */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<Appointment> create(
            @Valid @RequestBody AppointmentDtos.CreateAppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.create(request));
    }

    /** PATCH /api/turnos/{id}/confirmar */
    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<Appointment> confirm(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.confirm(id));
    }

    /** PATCH /api/turnos/{id}/cancelar */
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    public ResponseEntity<Appointment> cancel(
            @PathVariable UUID id,
            @Valid @RequestBody AppointmentDtos.CancelAppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.cancel(id, request));
    }

    /** PATCH /api/turnos/{id}/completar */
    @PatchMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Appointment> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.complete(id));
    }

    /** PATCH /api/turnos/{id}/ausente */
    @PatchMapping("/{id}/ausente")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<Appointment> noShow(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.markNoShow(id));
    }
}
