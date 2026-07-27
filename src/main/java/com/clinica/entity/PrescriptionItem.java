package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "prescription_items")
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class PrescriptionItem extends BaseEntity {

    @NotBlank
    @Column(name = "medication_name", nullable = false, length = 200)
    private String medicationName;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String dosage;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String frequency;

    @Column(length = 100)
    private String duration;

    @Column(length = 255)
    private String instructions;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;
}
