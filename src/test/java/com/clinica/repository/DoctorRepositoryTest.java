package com.clinica.repository;

import com.clinica.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DoctorRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired DoctorRepository  doctorRepository;

    private Specialty persistSpecialty(String name) {
        return em.persist(Specialty.builder()
                .name(name).active(true).build());
    }

    private Doctor persistDoctor(String license, String firstName,
                                  String lastName, Specialty spec, boolean active) {
        Doctor d = Doctor.builder()
                .licenseNumber(license)
                .firstName(firstName).lastName(lastName)
                .consultationFeeMinutes(30)
                .active(active)
                .specialty(spec)
                .build();
        return em.persist(d);
    }

    @Test
    @DisplayName("findAllByActiveTrueAndDeletedAtIsNull solo retorna activos")
    void findAllActive_ok() {
        Specialty spec = persistSpecialty("Pediatría");
        persistDoctor("MP-001", "Ana",   "Torres", spec, true);
        persistDoctor("MP-002", "Luis",  "Vera",   spec, false);
        em.flush();

        List<Doctor> result = doctorRepository.findAllByActiveTrueAndDeletedAtIsNull();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLicenseNumber()).isEqualTo("MP-001");
    }

    @Test
    @DisplayName("findAllByActiveTrueAndDeletedAtIsNull excluye soft-deleted")
    void findAllActive_excludesDeleted() {
        Specialty spec = persistSpecialty("Cardiología");
        Doctor deleted = persistDoctor("MP-003", "Rosa", "Díaz", spec, true);
        deleted.setDeletedAt(java.time.LocalDateTime.now());
        em.persist(deleted);
        em.flush();

        List<Doctor> result = doctorRepository.findAllByActiveTrueAndDeletedAtIsNull();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByLicenseNumberAndDeletedAtIsNull retorna médico activo")
    void findByLicense_ok() {
        Specialty spec = persistSpecialty("Neurología");
        persistDoctor("MP-004", "Marcos", "Gil", spec, true);
        em.flush();

        Optional<Doctor> result = doctorRepository
                .findByLicenseNumberAndDeletedAtIsNull("MP-004");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Marcos");
    }

    @Test
    @DisplayName("existsByLicenseNumberAndDeletedAtIsNull retorna true si existe")
    void existsByLicense_true() {
        Specialty spec = persistSpecialty("Dermatología");
        persistDoctor("MP-005", "Sofía", "Molina", spec, true);
        em.flush();

        assertThat(doctorRepository
                .existsByLicenseNumberAndDeletedAtIsNull("MP-005")).isTrue();
    }

    @Test
    @DisplayName("findBySpecialtyId filtra por especialidad")
    void findBySpecialty_ok() {
        Specialty clinica  = persistSpecialty("Clínica Médica");
        Specialty cardio   = persistSpecialty("Cardiología 2");

        persistDoctor("MP-006", "Hugo",  "Ríos",   clinica, true);
        persistDoctor("MP-007", "Elena", "Blanco", clinica, true);
        persistDoctor("MP-008", "Mario", "Castro", cardio,  true);
        em.flush();

        List<Doctor> result = doctorRepository.findBySpecialtyId(clinica.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Doctor::getLastName)
                .containsExactlyInAnyOrder("Ríos", "Blanco");
    }

    @Test
    @DisplayName("findBySpecialtyId no incluye inactivos")
    void findBySpecialty_excludesInactive() {
        Specialty spec = persistSpecialty("Ginecología");
        persistDoctor("MP-009", "Paula", "Núñez", spec, false);
        em.flush();

        List<Doctor> result = doctorRepository.findBySpecialtyId(spec.getId());

        assertThat(result).isEmpty();
    }
}
