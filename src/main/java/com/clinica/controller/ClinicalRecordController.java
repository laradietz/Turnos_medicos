package com.clinica.controller;

import com.clinica.entity.*;
import com.clinica.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// ── HISTORIA CLÍNICA ─────────────────────────────────────────
@RestController
@RequestMapping("/api/historia-clinica")
@RequiredArgsConstructor
public class ClinicalRecordController {

    private final ClinicalRecordService clinicalRecordService;

    /** GET /api/historia-clinica/paciente/{patientId} */
    @GetMapping("/paciente/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<ClinicalRecord> findByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(clinicalRecordService.findByPatient(patientId));
    }

    /**
     * PATCH /api/historia-clinica/paciente/{patientId}
     * Body: { "allergies": "...", "chronicConditions": "...", ... }
     */
    @PatchMapping("/paciente/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<ClinicalRecord> update(
            @PathVariable UUID patientId,
            @RequestBody Map<String, String> fields) {
        return ResponseEntity.ok(clinicalRecordService.updateNotes(
                patientId,
                fields.get("allergies"),
                fields.get("chronicConditions"),
                fields.get("currentMedications"),
                fields.get("familyHistory"),
                fields.get("surgicalHistory"),
                fields.get("notes")
        ));
    }
}
