package com.clinica.controller;

import com.clinica.dto.MedicalStudyDtos;
import com.clinica.entity.*;
import com.clinica.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// ── ESTUDIOS MÉDICOS ─────────────────────────────────────────
@RestController
@RequestMapping("/api/estudios")
@RequiredArgsConstructor
public class MedicalStudyController {

    private final MedicalStudyService medicalStudyService;

    /** GET /api/estudios/paciente/{patientId} */
    @GetMapping("/paciente/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<List<MedicalStudy>> findByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(medicalStudyService.findByPatient(patientId));
    }

    /** POST /api/estudios — Solicitar estudio */
    @PostMapping
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<MedicalStudy> create(
            @Valid @RequestBody MedicalStudyDtos.CreateMedicalStudyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicalStudyService.create(request));
    }

    /** PATCH /api/estudios/{id}/resultado — Cargar resultado */
    @PatchMapping("/{id}/resultado")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<MedicalStudy> updateResult(
            @PathVariable UUID id,
            @Valid @RequestBody MedicalStudyDtos.UpdateStudyResultRequest request) {
        return ResponseEntity.ok(medicalStudyService.updateResult(id, request));
    }
}
