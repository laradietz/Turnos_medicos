package com.clinica.service;

import com.clinica.dto.AppointmentDtos;
import com.clinica.entity.*;
import com.clinica.exception.BusinessException;
import com.clinica.exception.ResourceNotFoundException;
import com.clinica.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock AppointmentRepository             appointmentRepository;
    @Mock PatientRepository                 patientRepository;
    @Mock DoctorRepository                  doctorRepository;
    @Mock OfficeRepository                  officeRepository;
    @Mock HealthInsuranceRepository         healthInsuranceRepository;
    @Mock AppointmentStatusHistoryRepository statusHistoryRepository;
    @Mock SystemConfigurationRepository     configRepository;

    @InjectMocks AppointmentService appointmentService;

    private UUID patientId = UUID.randomUUID();
    private UUID doctorId  = UUID.randomUUID();

    private Patient buildPatient() {
        return Patient.builder()
                .firstName("Ana").lastName("García")
                .active(true).build();
    }

    private Doctor buildDoctor() {
        return Doctor.builder()
                .firstName("Carlos").lastName("López")
                .consultationFeeMinutes(30)
                .active(true).build();
    }

    private AppointmentDtos.CreateAppointmentRequest buildRequest() {
        return new AppointmentDtos.CreateAppointmentRequest(
                patientId, doctorId, null, null,
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                Appointment.AppointmentType.FIRST_VISIT,
                "Primera consulta"
        );
    }

    @Test
    @DisplayName("Crear turno exitosamente")
    void create_ok() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(buildPatient()));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(buildDoctor()));
        when(appointmentRepository.findConflict(any(), any(), any())).thenReturn(Optional.empty());
        when(configRepository.findByConfigKey("max_appointments_per_day")).thenReturn(Optional.empty());
        when(appointmentRepository.countByDoctorAndDate(any(), any())).thenReturn(0L);
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(statusHistoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.create(buildRequest());

        assertThat(result.getStatus()).isEqualTo(Appointment.AppointmentStatus.PENDING);
        assertThat(result.getStartTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.getEndTime()).isEqualTo(LocalTime.of(9, 30));
    }

    @Test
    @DisplayName("No se puede crear turno con conflicto de horario")
    void create_conflict_throwsBusiness() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(buildPatient()));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(buildDoctor()));
        when(appointmentRepository.findConflict(any(), any(), any()))
                .thenReturn(Optional.of(new Appointment()));

        assertThatThrownBy(() -> appointmentService.create(buildRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already has an appointment");
    }

    @Test
    @DisplayName("Transición inválida PENDING → COMPLETED lanza excepción")
    void complete_invalidTransition_throws() {
        UUID id = UUID.randomUUID();
        Appointment appt = Appointment.builder()
                .status(Appointment.AppointmentStatus.PENDING).build();

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appt));

        assertThatThrownBy(() -> appointmentService.complete(id))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid status transition");
    }

    @Test
    @DisplayName("Confirmar turno PENDING → CONFIRMED")
    void confirm_ok() {
        UUID id = UUID.randomUUID();
        Appointment appt = Appointment.builder()
                .status(Appointment.AppointmentStatus.PENDING).build();

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appt));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(statusHistoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.confirm(id);

        assertThat(result.getStatus()).isEqualTo(Appointment.AppointmentStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Cancelar turno CONFIRMED → CANCELLED")
    void cancel_ok() {
        UUID id = UUID.randomUUID();
        Appointment appt = Appointment.builder()
                .status(Appointment.AppointmentStatus.CONFIRMED).build();

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appt));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(statusHistoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new AppointmentDtos.CancelAppointmentRequest("El paciente no puede asistir");
        Appointment result = appointmentService.cancel(id, req);

        assertThat(result.getStatus()).isEqualTo(Appointment.AppointmentStatus.CANCELLED);
        assertThat(result.getCancellationReason()).isEqualTo("El paciente no puede asistir");
    }
}
