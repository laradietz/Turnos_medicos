package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "roles",
       uniqueConstraints = @UniqueConstraint(name = "uk_role_name", columnNames = "name"))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Role extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 150)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<User> users;
}
