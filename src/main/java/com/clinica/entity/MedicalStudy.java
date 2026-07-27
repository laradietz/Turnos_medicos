package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "medical_studies")
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class MedicalStudy extends BaseEntity {

    public enum StudyStatus { ORDERED, PENDING, COMPLETED, CANCELLED }

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "study_type", length = 100)
    private String studyType;

    @Column(name = "ordered_date", nullable = false)
    private LocalDate orderedDate;

    @Column(name = "result_date")
    private LocalDate resultDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudyStatus status;

    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(length = 255)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinical_record_id", nullable = false)
    private ClinicalRecord clinicalRecord;
}
