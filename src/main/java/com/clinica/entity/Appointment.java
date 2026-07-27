package com.clinica.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "appointments",
       indexes = {
           @Index(name = "idx_appointment_doctor_date", columnList = "doctor_id, appointment_date"),
           @Index(name = "idx_appointment_patient",     columnList = "patient_id"),
           @Index(name = "idx_appointment_status",      columnList = "status")
       })
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Appointment extends SoftDeleteEntity {

    public enum AppointmentStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW, RESCHEDULED
    }

    public enum AppointmentType {
        FIRST_VISIT, FOLLOW_UP, EMERGENCY, CHECKUP
    }

    @NotNull
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentType type;

    @Column(length = 500)
    private String notes;

    @Column(name = "cancellation_reason", length = 255)
    private String cancellationReason;

    @Column(name = "reminder_sent", nullable = false)
    private Boolean reminderSent;

    // EAGER: necesarios en (casi) toda respuesta con turnos, y evita
    // LazyInitializationException al serializar con open-in-view=false
    // (p.ej. GET /api/turnos/{id}, que no usa un JOIN FETCH explícito).
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private Office office;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_insurance_id")
    private HealthInsurance healthInsurance;

    @JsonIgnore
    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY,
              cascade = CascadeType.ALL, orphanRemoval = true)
    private Consultation consultation;

    @JsonIgnore
    @OneToMany(mappedBy = "appointment", fetch = FetchType.LAZY)
    private List<AppointmentStatusHistory> statusHistories;

    @JsonIgnore
    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY)
    private Payment payment;
}
