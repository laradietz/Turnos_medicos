package com.clinica.controller;

import com.clinica.dto.HealthInsuranceDtos;
import com.clinica.entity.*;
import com.clinica.repository.HealthInsuranceRepository;
import com.clinica.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ── HEALTH INSURANCE CONTROLLER ──────────────────────────────
@RestController
@RequestMapping("/api/health-insurances")
@RequiredArgsConstructor
public class HealthInsuranceController {

    private final HealthInsuranceRepository healthInsuranceRepository;

    @GetMapping
    public ResponseEntity<List<HealthInsurance>> findAll() {
        return ResponseEntity.ok(healthInsuranceRepository.findAllByActiveTrueAndDeletedAtIsNull());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HealthInsurance> create(
            @Valid @RequestBody HealthInsuranceDtos.CreateHealthInsuranceRequest request) {
        if (healthInsuranceRepository.existsByNameAndDeletedAtIsNull(request.name())) {
            throw new com.clinica.exception.BusinessException("Health insurance already exists: " + request.name());
        }
        HealthInsurance hi = HealthInsurance.builder()
                .name(request.name())
                .code(request.code())
                .address(request.address())
                .phone(request.phone())
                .active(true)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(healthInsuranceRepository.save(hi));
    }
}
