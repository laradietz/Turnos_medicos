package com.clinica.controller;

import com.clinica.dto.*;
import com.clinica.entity.*;
import com.clinica.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// ── DOCTOR CONTROLLER ────────────────────────────────────────
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<Doctor>> findAll(
            @RequestParam(required = false) UUID specialtyId) {
        List<Doctor> result = specialtyId != null
                ? doctorService.findBySpecialty(specialtyId)
                : doctorService.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Doctor> create(
            @Valid @RequestBody DoctorDtos.CreateDoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Doctor> update(
            @PathVariable UUID id,
            @Valid @RequestBody DoctorDtos.UpdateDoctorRequest request) {
        return ResponseEntity.ok(doctorService.update(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        doctorService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
