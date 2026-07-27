package com.clinica.repository;

import com.clinica.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Query("""
        SELECT a FROM Appointment a JOIN FETCH a.patient JOIN FETCH a.doctor
        WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.deletedAt IS NULL
        ORDER BY a.startTime
    """)
    List<Appointment> findByDoctorAndDate(@Param("doctorId") UUID doctorId,
                                           @Param("date") LocalDate date);

    @Query("""
        SELECT a FROM Appointment a JOIN FETCH a.patient JOIN FETCH a.doctor
        WHERE a.doctor.id = :doctorId
          AND a.appointmentDate BETWEEN :from AND :to
          AND a.status <> 'CANCELLED' AND a.deletedAt IS NULL
        ORDER BY a.appointmentDate, a.startTime
    """)
    List<Appointment> findByDoctorAndDateRange(@Param("doctorId") UUID doctorId,
                                                @Param("from") LocalDate from,
                                                @Param("to") LocalDate to);

    @Query("""
        SELECT a FROM Appointment a JOIN FETCH a.doctor
        WHERE a.patient.id = :patientId AND a.deletedAt IS NULL
        ORDER BY a.appointmentDate DESC, a.startTime DESC
    """)
    List<Appointment> findByPatientId(@Param("patientId") UUID patientId);

    @Query("""
        SELECT COUNT(a) FROM Appointment a
        WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date
          AND a.status <> 'CANCELLED' AND a.deletedAt IS NULL
    """)
    long countByDoctorAndDate(@Param("doctorId") UUID doctorId, @Param("date") LocalDate date);

    @Query("""
        SELECT a FROM Appointment a
        WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date
          AND a.startTime = :startTime AND a.status <> 'CANCELLED' AND a.deletedAt IS NULL
    """)
    Optional<Appointment> findConflict(@Param("doctorId") UUID doctorId,
                                        @Param("date") LocalDate date,
                                        @Param("startTime") LocalTime startTime);
}
