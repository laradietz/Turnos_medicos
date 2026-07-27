package com.clinica.controller;

import com.clinica.dto.SpecialtyDtos;
import com.clinica.entity.*;
import com.clinica.repository.SpecialtyRepository;
import com.clinica.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ── SPECIALTY CONTROLLER ─────────────────────────────────────
@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyRepository specialtyRepository;

    @GetMapping
    public ResponseEntity<List<Specialty>> findAll() {
        return ResponseEntity.ok(specialtyRepository.findAllByActiveTrueAndDeletedAtIsNull());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Specialty> create(
            @Valid @RequestBody SpecialtyDtos.CreateSpecialtyRequest request) {
        if (specialtyRepository.existsByNameAndDeletedAtIsNull(request.name())) {
            throw new com.clinica.exception.BusinessException("Specialty already exists: " + request.name());
        }
        Specialty specialty = Specialty.builder()
                .name(request.name())
                .description(request.description())
                .active(true)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(specialtyRepository.save(specialty));
    }
}
