package com.clinica.service;

import com.clinica.dto.PatientDtos;
import com.clinica.entity.Patient;
import com.clinica.exception.BusinessException;
import com.clinica.exception.ResourceNotFoundException;
import com.clinica.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock PatientRepository          patientRepository;
    @Mock PatientInsuranceRepository patientInsuranceRepository;
    @Mock HealthInsuranceRepository  healthInsuranceRepository;
    @Mock ClinicalRecordRepository   clinicalRecordRepository;

    @InjectMocks PatientService patientService;

    private PatientDtos.CreatePatientRequest validRequest() {
        return new PatientDtos.CreatePatientRequest(
                "12345678",
                "Juan",
                "Pérez",
                LocalDate.of(1990, 1, 15),
                Patient.Gender.MALE,
                "juan@mail.com",
                "1155667788",
                "Av. Corrientes 1234",
                "Buenos Aires",
                Patient.BloodType.O_POS,
                "María Pérez",
                "1144556677"
        );
    }

    @Test
    @DisplayName("Crear paciente exitosamente")
    void create_ok() {
        when(patientRepository.existsByDniAndDeletedAtIsNull("12345678")).thenReturn(false);
        when(patientRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(clinicalRecordRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Patient result = patientService.create(validRequest());

        assertThat(result.getDni()).isEqualTo("12345678");
        assertThat(result.getFirstName()).isEqualTo("Juan");
        assertThat(result.getActive()).isTrue();
        verify(clinicalRecordRepository).save(any());
    }

    @Test
    @DisplayName("No se puede crear paciente con DNI duplicado")
    void create_duplicateDni_throwsBusiness() {
        when(patientRepository.existsByDniAndDeletedAtIsNull("12345678")).thenReturn(true);

        assertThatThrownBy(() -> patientService.create(validRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("12345678");
    }

    @Test
    @DisplayName("findById lanza excepción si no existe")
    void findById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Soft delete setea deletedAt")
    void delete_setsDeletedAt() {
        UUID id = UUID.randomUUID();
        Patient patient = Patient.builder()
                .dni("12345678").firstName("Juan").lastName("Pérez")
                .birthDate(LocalDate.of(1990, 1, 15)).active(true).build();

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        patientService.delete(id);

        assertThat(patient.getDeletedAt()).isNotNull();
        verify(patientRepository).save(patient);
    }
}
