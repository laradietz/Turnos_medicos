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
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
    @Query("""
        SELECT a FROM Availability a
        WHERE a.doctor.id = :doctorId AND a.date BETWEEN :from AND :to
        ORDER BY a.date, a.startTime
    """)
    List<Availability> findByDoctorAndDateRange(@Param("doctorId") UUID doctorId,
                                                 @Param("from") LocalDate from,
                                                 @Param("to") LocalDate to);
}
