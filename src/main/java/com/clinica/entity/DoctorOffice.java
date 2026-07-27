package com.clinica.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "doctor_offices",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_doctor_office",
           columnNames = {"doctor_id", "office_id", "valid_from"}))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class DoctorOffice extends BaseEntity {

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;
}
