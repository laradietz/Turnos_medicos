package com.clinica.controller;

import com.clinica.entity.*;
import com.clinica.exception.ResourceNotFoundException;
import com.clinica.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// ── NOTIFICACIONES ───────────────────────────────────────────
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService    notificationService;
    private final com.clinica.repository.UserRepository userRepository;

    /** GET /api/notificaciones — Las del usuario autenticado */
    @GetMapping
    public ResponseEntity<List<Notification>> findMine(
            Authentication authentication) {
        UUID userId = resolveUserId(authentication.getName());
        return ResponseEntity.ok(notificationService.findAllByUser(userId));
    }

    /** GET /api/notificaciones/no-leidas */
    @GetMapping("/no-leidas")
    public ResponseEntity<List<Notification>> findUnread(
            Authentication authentication) {
        UUID userId = resolveUserId(authentication.getName());
        return ResponseEntity.ok(notificationService.findUnreadByUser(userId));
    }

    /** GET /api/notificaciones/no-leidas/cantidad */
    @GetMapping("/no-leidas/cantidad")
    public ResponseEntity<Map<String, Long>> countUnread(
            Authentication authentication) {
        UUID userId = resolveUserId(authentication.getName());
        return ResponseEntity.ok(Map.of("cantidad", notificationService.countUnread(userId)));
    }

    /** PATCH /api/notificaciones/{id}/leer */
    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    /** PATCH /api/notificaciones/leer-todas */
    @PatchMapping("/leer-todas")
    public ResponseEntity<Void> markAllAsRead(
            Authentication authentication) {
        UUID userId = resolveUserId(authentication.getName());
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    private UUID resolveUserId(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
    }
}
