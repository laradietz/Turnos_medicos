package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "consultations")
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Consultation extends BaseEntity {

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "treatment", columnDefinition = "TEXT")
    private String treatment;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "height_cm", precision = 5, scale = 1)
    private BigDecimal heightCm;

    @Column(name = "blood_pressure", length = 20)
    private String bloodPressure;

    @Column(name = "temperature", precision = 4, scale = 1)
    private BigDecimal temperature;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinical_record_id", nullable = false)
    private ClinicalRecord clinicalRecord;

    @OneToMany(mappedBy = "consultation", fetch = FetchType.LAZY)
    private List<Prescription> prescriptions;

    @OneToMany(mappedBy = "consultation", fetch = FetchType.LAZY)
    private List<MedicalStudy> studies;
}
