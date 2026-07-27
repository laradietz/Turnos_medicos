package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "health_insurances",
       uniqueConstraints = @UniqueConstraint(name = "uk_health_insurance_name", columnNames = "name"))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class HealthInsurance extends SoftDeleteEntity {

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 50)
    private String code;

    @Column(length = 255)
    private String address;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "healthInsurance", fetch = FetchType.LAZY)
    private List<PatientInsurance> patientInsurances;
}
