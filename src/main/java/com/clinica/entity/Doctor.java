package com.clinica.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "doctors",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_doctor_license", columnNames = "license_number"),
           @UniqueConstraint(name = "uk_doctor_user",    columnNames = "user_id")
       })
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Doctor extends SoftDeleteEntity {

    @NotBlank
    @Column(name = "license_number", nullable = false, length = 30)
    private String licenseNumber;

    @NotBlank
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Email
    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String biography;

    @Column(name = "consultation_fee_minutes", nullable = false)
    private Integer consultationFeeMinutes;

    @Column(nullable = false)
    private Boolean active;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // EAGER: se necesita en la mayoría de las respuestas (listados de médicos, turnos, etc.)
    // y evita LazyInitializationException al serializar con open-in-view=false.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<DoctorOffice> offices;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Appointment> appointments;
}
