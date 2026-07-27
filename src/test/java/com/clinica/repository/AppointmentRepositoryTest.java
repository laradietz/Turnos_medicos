package com.clinica.repository;

import com.clinica.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AppointmentRepositoryTest {

    @Autowired TestEntityManager    em;
    @Autowired AppointmentRepository appointmentRepository;

    private Specialty buildSpecialty() {
        return em.persist(Specialty.builder()
                .name("Clínica Médica").active(true).build());
    }

    private Doctor buildDoctor(Specialty spec) {
        return em.persist(Doctor.builder()
                .licenseNumber("MP-TEST-01")
                .firstName("Pedro").lastName("Soto")
                .consultationFeeMinutes(30)
                .active(true)
                .specialty(spec)
                .build());
    }

    private Patient buildPatient() {
        return em.persist(Patient.builder()
                .dni("30999888")
                .firstName("Laura").lastName("Gómez")
                .birthDate(LocalDate.of(1988, 7, 12))
                .active(true)
                .build());
    }

    private Appointment buildAppointment(Doctor doctor, Patient patient,
                                          LocalDate date, LocalTime start,
                                          Appointment.AppointmentStatus status) {
        return em.persist(Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDate(date)
                .startTime(start)
                .endTime(start.plusMinutes(30))
                .status(status)
                .type(Appointment.AppointmentType.FIRST_VISIT)
                .reminderSent(false)
                .build());
    }

    @Test
    @DisplayName("findByDoctorAndDate retorna turnos del día")
    void findByDoctorAndDate_ok() {
        Specialty spec   = buildSpecialty();
        Doctor    doctor = buildDoctor(spec);
        Patient   patient = buildPatient();
        LocalDate date   = LocalDate.now().plusDays(1);

        buildAppointment(doctor, patient, date, LocalTime.of(9, 0),
                Appointment.AppointmentStatus.PENDING);
        buildAppointment(doctor, patient, date, LocalTime.of(9, 30),
                Appointment.AppointmentStatus.CONFIRMED);
        em.flush();

        List<Appointment> result = appointmentRepository.findByDoctorAndDate(doctor.getId(), date);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(a -> a.getStartTime())
                .containsExactlyInAnyOrder(LocalTime.of(9, 0), LocalTime.of(9, 30));
    }

    @Test
    @DisplayName("findByDoctorAndDate no incluye soft-deleted")
    void findByDoctorAndDate_excludesDeleted() {
        Specialty spec   = buildSpecialty();
        Doctor    doctor = buildDoctor(spec);
        Patient   patient = buildPatient();
        LocalDate date   = LocalDate.now().plusDays(2);

        Appointment deleted = buildAppointment(doctor, patient, date,
                LocalTime.of(10, 0), Appointment.AppointmentStatus.PENDING);
        deleted.setDeletedAt(LocalDateTime.now());
        em.persist(deleted);
        em.flush();

        List<Appointment> result = appointmentRepository.findByDoctorAndDate(doctor.getId(), date);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findConflict detecta turno en el mismo horario")
    void findConflict_exists() {
        Specialty spec   = buildSpecialty();
        Doctor    doctor = buildDoctor(spec);
        Patient   patient = buildPatient();
        LocalDate date   = LocalDate.now().plusDays(3);
        LocalTime time   = LocalTime.of(11, 0);

        buildAppointment(doctor, patient, date, time, Appointment.AppointmentStatus.CONFIRMED);
        em.flush();

        Optional<Appointment> conflict = appointmentRepository
                .findConflict(doctor.getId(), date, time);

        assertThat(conflict).isPresent();
    }

    @Test
    @DisplayName("findConflict ignora turnos cancelados")
    void findConflict_ignoresCancelled() {
        Specialty spec   = buildSpecialty();
        Doctor    doctor = buildDoctor(spec);
        Patient   patient = buildPatient();
        LocalDate date   = LocalDate.now().plusDays(4);
        LocalTime time   = LocalTime.of(14, 0);

        buildAppointment(doctor, patient, date, time, Appointment.AppointmentStatus.CANCELLED);
        em.flush();

        Optional<Appointment> conflict = appointmentRepository
                .findConflict(doctor.getId(), date, time);

        assertThat(conflict).isEmpty();
    }

    @Test
    @DisplayName("countByDoctorAndDate cuenta solo activos no cancelados")
    void countByDoctorAndDate_ok() {
        Specialty spec   = buildSpecialty();
        Doctor    doctor = buildDoctor(spec);
        Patient   patient = buildPatient();
        LocalDate date   = LocalDate.now().plusDays(5);

        buildAppointment(doctor, patient, date, LocalTime.of(8,  0), Appointment.AppointmentStatus.PENDING);
        buildAppointment(doctor, patient, date, LocalTime.of(8, 30), Appointment.AppointmentStatus.CONFIRMED);
        buildAppointment(doctor, patient, date, LocalTime.of(9,  0), Appointment.AppointmentStatus.CANCELLED);
        em.flush();

        long count = appointmentRepository.countByDoctorAndDate(doctor.getId(), date);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("findByPatientId retorna historial del paciente ordenado")
    void findByPatientId_ok() {
        Specialty spec   = buildSpecialty();
        Doctor    doctor = buildDoctor(spec);
        Patient   patient = buildPatient();

        buildAppointment(doctor, patient,
                LocalDate.now().minusDays(5), LocalTime.of(9, 0),
                Appointment.AppointmentStatus.COMPLETED);
        buildAppointment(doctor, patient,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                Appointment.AppointmentStatus.PENDING);
        em.flush();

        List<Appointment> result = appointmentRepository.findByPatientId(patient.getId());

        assertThat(result).hasSize(2);
        // ordenado por fecha DESC: el futuro primero
        assertThat(result.get(0).getAppointmentDate())
                .isAfter(result.get(1).getAppointmentDate());
    }
}
