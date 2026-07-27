package com.clinica.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "specialties",
       uniqueConstraints = @UniqueConstraint(name = "uk_specialty_name", columnNames = "name"))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Specialty extends SoftDeleteEntity {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean active;

    @JsonIgnore
    @OneToMany(mappedBy = "specialty", fetch = FetchType.LAZY)
    private List<Doctor> doctors;
}
