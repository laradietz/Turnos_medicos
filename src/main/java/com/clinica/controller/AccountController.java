package com.clinica.controller;

import com.clinica.dto.UserDtos;
import com.clinica.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// ── MI CUENTA (self-service) ─────────────────────────────────
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /** GET /api/account/me */
    @GetMapping("/me")
    public ResponseEntity<UserDtos.UserResponse> getMyAccount(Authentication authentication) {
        return ResponseEntity.ok(accountService.getMyAccount(authentication.getName()));
    }

    /**
     * PATCH /api/account/me
     * Cambiar nombre de usuario y/o contraseña. Requiere la contraseña actual.
     */
    @PatchMapping("/me")
    public ResponseEntity<UserDtos.UserResponse> updateMyAccount(
            Authentication authentication,
            @Valid @RequestBody UserDtos.UpdateMyAccountRequest request) {
        return ResponseEntity.ok(
                accountService.updateMyAccount(authentication.getName(), request));
    }
}
