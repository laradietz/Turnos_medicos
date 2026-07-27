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
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByDniAndDeletedAtIsNull(String dni);
    boolean existsByDniAndDeletedAtIsNull(String dni);

    @Query("""
        SELECT p FROM Patient p
        WHERE p.deletedAt IS NULL
          AND (:name IS NULL
            OR LOWER(p.lastName)  LIKE LOWER(CONCAT('%',:name,'%'))
            OR LOWER(p.firstName) LIKE LOWER(CONCAT('%',:name,'%')))
        ORDER BY p.lastName, p.firstName
    """)
    List<Patient> searchByName(@Param("name") String name);
}
