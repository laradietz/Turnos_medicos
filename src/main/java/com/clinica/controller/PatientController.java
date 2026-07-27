package com.clinica.controller;

import com.clinica.dto.PatientDtos;
import com.clinica.entity.*;
import com.clinica.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    public ResponseEntity<List<Patient>> findAll(
            @RequestParam(required = false) String name) {
        List<Patient> result = name != null
                ? patientService.search(name)
                : patientService.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    public ResponseEntity<Patient> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<Patient> create(
            @Valid @RequestBody PatientDtos.CreatePatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<Patient> update(
            @PathVariable UUID id,
            @Valid @RequestBody PatientDtos.UpdatePatientRequest request) {
        return ResponseEntity.ok(patientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/insurances")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    public ResponseEntity<List<PatientInsurance>> findInsurances(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.findInsurances(id));
    }

    @PostMapping("/{id}/insurances")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<PatientInsurance> addInsurance(
            @PathVariable UUID id,
            @Valid @RequestBody PatientDtos.PatientInsuranceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.addInsurance(id, request));
    }
}
