package com.clinica.repository;

import com.clinica.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PatientRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired PatientRepository patientRepository;

    private Patient buildPatient(String dni, String firstName, String lastName) {
        return Patient.builder()
                .dni(dni)
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Patient.Gender.FEMALE)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("findByDniAndDeletedAtIsNull retorna paciente activo")
    void findByDni_active() {
        em.persist(buildPatient("20111222", "María", "López"));
        em.flush();

        Optional<Patient> result = patientRepository.findByDniAndDeletedAtIsNull("20111222");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("María");
    }

    @Test
    @DisplayName("findByDniAndDeletedAtIsNull no retorna paciente con soft delete")
    void findByDni_deleted() {
        Patient p = buildPatient("20111333", "Carlos", "Ruiz");
        p.setDeletedAt(java.time.LocalDateTime.now());
        em.persist(p);
        em.flush();

        Optional<Patient> result = patientRepository.findByDniAndDeletedAtIsNull("20111333");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByDniAndDeletedAtIsNull retorna true si existe")
    void existsByDni_true() {
        em.persist(buildPatient("20222333", "Pedro", "García"));
        em.flush();

        assertThat(patientRepository.existsByDniAndDeletedAtIsNull("20222333")).isTrue();
    }

    @Test
    @DisplayName("existsByDniAndDeletedAtIsNull retorna false si no existe")
    void existsByDni_false() {
        assertThat(patientRepository.existsByDniAndDeletedAtIsNull("99999999")).isFalse();
    }

    @Test
    @DisplayName("searchByName encuentra por apellido parcial")
    void searchByName_byLastName() {
        em.persist(buildPatient("20333444", "Ana", "Fernández"));
        em.persist(buildPatient("20333445", "Luis", "Fernández"));
        em.flush();

        List<Patient> result = patientRepository.searchByName("Fernán");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Patient::getLastName)
                .allMatch(n -> n.contains("Fernández"));
    }

    @Test
    @DisplayName("searchByName encuentra por nombre parcial")
    void searchByName_byFirstName() {
        em.persist(buildPatient("20444555", "Valentina", "Sosa"));
        em.flush();

        List<Patient> result = patientRepository.searchByName("Valen");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Valentina");
    }

    @Test
    @DisplayName("searchByName es case-insensitive")
    void searchByName_caseInsensitive() {
        em.persist(buildPatient("20555666", "Rocío", "Mendoza"));
        em.flush();

        List<Patient> upper  = patientRepository.searchByName("MENDOZA");
        List<Patient> lower  = patientRepository.searchByName("mendoza");
        List<Patient> mixed  = patientRepository.searchByName("Mendoza");

        assertThat(upper).hasSize(1);
        assertThat(lower).hasSize(1);
        assertThat(mixed).hasSize(1);
    }

    @Test
    @DisplayName("searchByName no incluye pacientes con soft delete")
    void searchByName_excludesDeleted() {
        Patient deleted = buildPatient("20666777", "Jorge", "Ramos");
        deleted.setDeletedAt(java.time.LocalDateTime.now());
        em.persist(deleted);
        em.flush();

        List<Patient> result = patientRepository.searchByName("Ramos");

        assertThat(result).isEmpty();
    }
}
