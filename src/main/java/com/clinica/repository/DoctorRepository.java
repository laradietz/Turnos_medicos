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
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    List<Doctor> findAllByActiveTrueAndDeletedAtIsNull();
    Optional<Doctor> findByLicenseNumberAndDeletedAtIsNull(String licenseNumber);
    boolean existsByLicenseNumberAndDeletedAtIsNull(String licenseNumber);

    @Query("""
        SELECT d FROM Doctor d JOIN FETCH d.specialty s
        WHERE d.deletedAt IS NULL AND d.active = TRUE AND s.id = :specialtyId
        ORDER BY d.lastName
    """)
    List<Doctor> findBySpecialtyId(@Param("specialtyId") UUID specialtyId);
}
