package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "patient_insurances",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_patient_insurance",
           columnNames = {"patient_id", "health_insurance_id"}))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class PatientInsurance extends BaseEntity {

    @NotBlank
    @Column(name = "affiliate_number", nullable = false, length = 50)
    private String affiliateNumber;

    @Column(name = "plan", length = 80)
    private String plan;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_insurance_id", nullable = false)
    private HealthInsurance healthInsurance;
}
