package com.clinica.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "clinical_records",
       uniqueConstraints = @UniqueConstraint(name = "uk_clinical_record_patient",
                                              columnNames = "patient_id"))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class ClinicalRecord extends BaseEntity {

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "chronic_conditions", columnDefinition = "TEXT")
    private String chronicConditions;

    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;

    @Column(name = "family_history", columnDefinition = "TEXT")
    private String familyHistory;

    @Column(name = "surgical_history", columnDefinition = "TEXT")
    private String surgicalHistory;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    @OneToMany(mappedBy = "clinicalRecord", fetch = FetchType.LAZY)
    private List<Consultation> consultations;

    @OneToMany(mappedBy = "clinicalRecord", fetch = FetchType.LAZY)
    private List<Prescription> prescriptions;

    @OneToMany(mappedBy = "clinicalRecord", fetch = FetchType.LAZY)
    private List<MedicalStudy> studies;
}
