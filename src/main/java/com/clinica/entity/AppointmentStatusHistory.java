package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "appointment_status_histories")
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class AppointmentStatusHistory extends BaseEntity {

    // Sin @NotNull: cuando se crea el turno por primera vez no hay estado
    // anterior, así que este campo es null legítimamente (columna nullable).
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private Appointment.AppointmentStatus previousStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private Appointment.AppointmentStatus newStatus;

    @Column(length = 255)
    private String reason;

    @Column(name = "changed_by", length = 100)
    private String changedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;
}
