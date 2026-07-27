package com.clinica.controller;

import com.clinica.dto.PaymentDtos;
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

// ── PAGOS ────────────────────────────────────────────────────
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /** GET /api/pagos/turno/{appointmentId} */
    @GetMapping("/turno/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<Payment> findByAppointment(@PathVariable UUID appointmentId) {
        return ResponseEntity.ok(paymentService.findByAppointment(appointmentId));
    }

    /** GET /api/pagos?status=PENDING */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<List<Payment>> findByStatus(
            @RequestParam(defaultValue = "PENDING") Payment.PaymentStatus status) {
        return ResponseEntity.ok(paymentService.findByStatus(status));
    }

    /** POST /api/pagos — Registrar pago */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<Payment> create(
            @Valid @RequestBody PaymentDtos.CreatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.create(request));
    }

    /** PATCH /api/pagos/{id}/confirmar */
    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<Payment> confirm(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body) {
        String ref = body != null ? body.get("transactionReference") : null;
        return ResponseEntity.ok(paymentService.confirmPayment(id, ref));
    }

    /** PATCH /api/pagos/{id}/reembolsar */
    @PatchMapping("/{id}/reembolsar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Payment> refund(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.refund(id));
    }
}
