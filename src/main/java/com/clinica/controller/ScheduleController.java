package com.clinica.controller;

import com.clinica.dto.ScheduleDtos;
import com.clinica.entity.*;
import com.clinica.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// ── SCHEDULE CONTROLLER ──────────────────────────────────────
@RestController
@RequestMapping("/api/agenda")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /** GET /api/agenda/{doctorId} */
    @GetMapping("/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    public ResponseEntity<List<Schedule>> findByDoctor(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(scheduleService.findByDoctor(doctorId));
    }

    /** POST /api/agenda/{doctorId} */
    @PostMapping("/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Schedule> create(
            @PathVariable UUID doctorId,
            @Valid @RequestBody ScheduleDtos.CreateScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduleService.create(doctorId, request));
    }

    /** PATCH /api/agenda/{scheduleId}/desactivar */
    @PatchMapping("/{scheduleId}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID scheduleId) {
        scheduleService.deactivate(scheduleId);
        return ResponseEntity.noContent().build();
    }
}
