package com.clinica.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients",
       uniqueConstraints = @UniqueConstraint(name = "uk_patient_dni", columnNames = "dni"))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Patient extends SoftDeleteEntity {

    public enum Gender { MALE, FEMALE, OTHER }
    public enum BloodType { A_POS, A_NEG, B_POS, B_NEG, AB_POS, AB_NEG, O_POS, O_NEG }

    @NotBlank
    @Column(nullable = false, length = 20)
    private String dni;

    @NotBlank
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Email
    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 50)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", length = 10)
    private BloodType bloodType;

    @Column(name = "emergency_contact_name", length = 150)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 30)
    private String emergencyContactPhone;

    @Column(nullable = false)
    private Boolean active;

    @JsonIgnore
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<PatientInsurance> insurances;

    @JsonIgnore
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @JsonIgnore
    @OneToOne(mappedBy = "patient", fetch = FetchType.LAZY,
              cascade = CascadeType.ALL, orphanRemoval = true)
    private ClinicalRecord clinicalRecord;
}
