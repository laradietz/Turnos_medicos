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
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    @Query("""
        SELECT s FROM Schedule s
        WHERE s.doctor.id = :doctorId AND s.active = TRUE
          AND (s.validUntil IS NULL OR s.validUntil >= :date)
          AND s.validFrom <= :date
        ORDER BY s.dayOfWeek, s.startTime
    """)
    List<Schedule> findActiveByDoctorAndDate(@Param("doctorId") UUID doctorId,
                                              @Param("date") LocalDate date);
}
