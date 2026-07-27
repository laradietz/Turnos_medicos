package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "schedules",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_schedule_doctor_day",
           columnNames = {"doctor_id", "day_of_week", "valid_from"}))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Schedule extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 15)
    private DayOfWeek dayOfWeek;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "slot_duration_minutes", nullable = false)
    private Integer slotDurationMinutes;

    @Column(name = "valid_from", nullable = false)
    private java.time.LocalDate validFrom;

    @Column(name = "valid_until")
    private java.time.LocalDate validUntil;

    @Column(nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private Office office;
}
