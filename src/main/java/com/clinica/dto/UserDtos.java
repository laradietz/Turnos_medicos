package com.clinica.dto;

import com.clinica.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserDtos {

    public record CreateUserRequest(
        @NotBlank @Size(min = 3, max = 80) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phone,
        @NotNull UUID roleId
    ) {}

    public record UpdateUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phone
    ) {}

    public record UserResponse(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        Boolean active,
        String roleName,
        LocalDateTime createdAt
    ) {}

    /** Para el propio usuario logueado, actualizando su cuenta ("Mi perfil"). */
    public record UpdateMyAccountRequest(
        @NotBlank String currentPassword,
        @Size(min = 3, max = 80) String newUsername,
        @Size(min = 8) String newPassword
    ) {}
}
