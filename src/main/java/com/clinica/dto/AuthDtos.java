package com.clinica.dto;

import com.clinica.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthDtos {

    public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
    ) {}

    public record LoginResponse(
        String token,
        String username,
        String role,
        long expiresIn
    ) {}
}
