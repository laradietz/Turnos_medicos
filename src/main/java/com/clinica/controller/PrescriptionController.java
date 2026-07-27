package com.clinica.controller;

import com.clinica.dto.PrescriptionDtos;
import com.clinica.entity.*;
import com.clinica.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// ── RECETAS ──────────────────────────────────────────────────
@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    /** GET /api/recetas/paciente/{patientId} */
    @GetMapping("/paciente/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<List<Prescription>> findByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(prescriptionService.findByPatient(patientId));
    }

    /** POST /api/recetas — Emitir receta */
    @PostMapping
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Prescription> create(
            @Valid @RequestBody PrescriptionDtos.CreatePrescriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prescriptionService.create(request));
    }
}
