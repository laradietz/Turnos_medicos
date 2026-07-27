package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "permissions",
       uniqueConstraints = @UniqueConstraint(name = "uk_permission_code", columnNames = "code"))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Permission extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 80)
    private String code;

    @Column(length = 150)
    private String description;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles;
}
