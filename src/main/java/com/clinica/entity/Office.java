package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "offices",
       uniqueConstraints = @UniqueConstraint(name = "uk_office_number", columnNames = "number"))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Office extends SoftDeleteEntity {

    @NotBlank
    @Column(nullable = false, length = 20)
    private String number;

    @Column(length = 100)
    private String description;

    @Column(length = 100)
    private String floor;

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "office", fetch = FetchType.LAZY)
    private List<DoctorOffice> doctorOffices;

    @OneToMany(mappedBy = "office", fetch = FetchType.LAZY)
    private List<Appointment> appointments;
}
