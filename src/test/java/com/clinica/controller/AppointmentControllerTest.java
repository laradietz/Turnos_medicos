package com.clinica.controller;

import com.clinica.dto.AppointmentDtos;
import com.clinica.entity.*;
import com.clinica.exception.BusinessException;
import com.clinica.security.JwtAuthenticationFilter;
import com.clinica.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.*;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = AppointmentController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class
    )
)
class AppointmentControllerTest {

    @Autowired MockMvc          mockMvc;
    @MockBean  AppointmentService appointmentService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private Appointment buildAppointment() {
        Patient patient = Patient.builder()
                .firstName("Ana").lastName("González").build();
        Doctor doctor = Doctor.builder()
                .firstName("Carlos").lastName("López")
                .consultationFeeMinutes(30).build();
        return Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(10, 30))
                .status(Appointment.AppointmentStatus.PENDING)
                .type(Appointment.AppointmentType.FIRST_VISIT)
                .reminderSent(false)
                .build();
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("GET /api/turnos → 200 con lista de turnos del día")
    void findByDoctorAndDate_ok() throws Exception {
        UUID doctorId = UUID.randomUUID();
        when(appointmentService.findByDoctor(eq(doctorId), any()))
                .thenReturn(List.of(buildAppointment()));

        mockMvc.perform(get("/api/turnos")
                        .param("doctorId", doctorId.toString())
                        .param("date", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("POST /api/turnos → 201 con turno creado")
    void create_ok() throws Exception {
        var req = new AppointmentDtos.CreateAppointmentRequest(
                UUID.randomUUID(), UUID.randomUUID(), null, null,
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                Appointment.AppointmentType.FIRST_VISIT,
                "Primera visita"
        );

        when(appointmentService.create(any())).thenReturn(buildAppointment());

        mockMvc.perform(post("/api/turnos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("POST /api/turnos → 422 por conflicto de horario")
    void create_conflict() throws Exception {
        var req = new AppointmentDtos.CreateAppointmentRequest(
                UUID.randomUUID(), UUID.randomUUID(), null, null,
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                Appointment.AppointmentType.FIRST_VISIT, null
        );

        when(appointmentService.create(any()))
                .thenThrow(new BusinessException("Doctor already has an appointment at that time"));

        mockMvc.perform(post("/api/turnos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Doctor already has an appointment at that time"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("PATCH /api/turnos/{id}/confirmar → 200")
    void confirm_ok() throws Exception {
        UUID id = UUID.randomUUID();
        Appointment confirmed = buildAppointment();
        confirmed.setStatus(Appointment.AppointmentStatus.CONFIRMED);

        when(appointmentService.confirm(id)).thenReturn(confirmed);

        mockMvc.perform(patch("/api/turnos/{id}/confirmar", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("PATCH /api/turnos/{id}/cancelar → 200 con motivo")
    void cancel_ok() throws Exception {
        UUID id = UUID.randomUUID();
        Appointment cancelled = buildAppointment();
        cancelled.setStatus(Appointment.AppointmentStatus.CANCELLED);
        cancelled.setCancellationReason("El paciente no puede asistir");

        when(appointmentService.cancel(eq(id), any())).thenReturn(cancelled);

        var req = new AppointmentDtos.CancelAppointmentRequest("El paciente no puede asistir");

        mockMvc.perform(patch("/api/turnos/{id}/cancelar", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.cancellationReason").value("El paciente no puede asistir"));
    }

    @Test
    @WithMockUser(roles = "MEDICO")
    @DisplayName("PATCH /api/turnos/{id}/completar → 200")
    void complete_ok() throws Exception {
        UUID id = UUID.randomUUID();
        Appointment completed = buildAppointment();
        completed.setStatus(Appointment.AppointmentStatus.COMPLETED);

        when(appointmentService.complete(id)).thenReturn(completed);

        mockMvc.perform(patch("/api/turnos/{id}/completar", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("PATCH /api/turnos/{id}/ausente → 200")
    void noShow_ok() throws Exception {
        UUID id = UUID.randomUUID();
        Appointment noShow = buildAppointment();
        noShow.setStatus(Appointment.AppointmentStatus.NO_SHOW);

        when(appointmentService.markNoShow(id)).thenReturn(noShow);

        mockMvc.perform(patch("/api/turnos/{id}/ausente", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NO_SHOW"));
    }
}
