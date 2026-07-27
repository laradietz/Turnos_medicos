package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
           @UniqueConstraint(name = "uk_user_email",    columnNames = "email")
       })
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class User extends SoftDeleteEntity {

    @NotBlank
    @Column(nullable = false, length = 80)
    private String username;

    @NotBlank
    @Email
    @Column(nullable = false, length = 150)
    private String email;

    @NotBlank
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false)
    private Boolean active;

    // EAGER: se lee en UserDetailsServiceImpl.loadUserByUsername() durante el login,
    // fuera de una transacción explícita; con open-in-view=false, LAZY aquí
    // provoca LazyInitializationException y el login falla con error 500.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Doctor doctor;
}
