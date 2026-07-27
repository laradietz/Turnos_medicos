package com.clinica.controller;

import com.clinica.dto.ConsultationDtos;
import com.clinica.entity.*;
import com.clinica.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// ── CONSULTAS ────────────────────────────────────────────────
@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    /** GET /api/consultas/paciente/{patientId} */
    @GetMapping("/paciente/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<List<Consultation>> findByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(consultationService.findByPatient(patientId));
    }

    /** GET /api/consultas/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Consultation> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(consultationService.findById(id));
    }

    /** POST /api/consultas — Registrar consulta médica */
    @PostMapping
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Consultation> create(
            @Valid @RequestBody ConsultationDtos.CreateConsultationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(consultationService.create(request));
    }
}
