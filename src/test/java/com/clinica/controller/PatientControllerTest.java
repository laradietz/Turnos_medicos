package com.clinica.controller;

import com.clinica.dto.PatientDtos;
import com.clinica.entity.Patient;
import com.clinica.exception.BusinessException;
import com.clinica.exception.ResourceNotFoundException;
import com.clinica.security.JwtAuthenticationFilter;
import com.clinica.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = PatientController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class
    )
)
class PatientControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean  PatientService patientService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private Patient buildPatient() {
        return Patient.builder()
                .dni("30123456")
                .firstName("Ana")
                .lastName("González")
                .birthDate(LocalDate.of(1985, 3, 20))
                .gender(Patient.Gender.FEMALE)
                .active(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("GET /api/patients → 200 con lista de pacientes")
    void findAll_ok() throws Exception {
        when(patientService.findAll()).thenReturn(List.of(buildPatient()));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].dni").value("30123456"))
                .andExpect(jsonPath("$[0].firstName").value("Ana"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("GET /api/patients?name=Ana → búsqueda por nombre")
    void search_ok() throws Exception {
        when(patientService.search("Ana")).thenReturn(List.of(buildPatient()));

        mockMvc.perform(get("/api/patients").param("name", "Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Ana"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("GET /api/patients/{id} → 404 si no existe")
    void findById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(patientService.findById(id))
                .thenThrow(new ResourceNotFoundException("Patient", id));

        mockMvc.perform(get("/api/patients/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("POST /api/patients → 201 con paciente creado")
    void create_ok() throws Exception {
        var req = new PatientDtos.CreatePatientRequest(
                "30123456", "Ana", "González",
                LocalDate.of(1985, 3, 20),
                Patient.Gender.FEMALE,
                "ana@mail.com", "1155443322",
                "Av. Santa Fe 1000", "Buenos Aires",
                Patient.BloodType.A_POS,
                "Carlos González", "1144332211"
        );

        when(patientService.create(any())).thenReturn(buildPatient());

        mockMvc.perform(post("/api/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("POST /api/patients → 422 si DNI duplicado")
    void create_duplicateDni() throws Exception {
        var req = new PatientDtos.CreatePatientRequest(
                "30123456", "Ana", "González",
                LocalDate.of(1985, 3, 20),
                Patient.Gender.FEMALE,
                null, null, null, null, null, null, null
        );

        when(patientService.create(any()))
                .thenThrow(new BusinessException("Patient with DNI 30123456 already exists"));

        mockMvc.perform(post("/api/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Business Error"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("POST /api/patients → 400 si faltan campos obligatorios")
    void create_validationError() throws Exception {
        // firstName vacío
        var req = new PatientDtos.CreatePatientRequest(
                "30123456", "", "González",
                LocalDate.of(1985, 3, 20),
                null, null, null, null, null, null, null, null
        );

        mockMvc.perform(post("/api/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/patients/{id} → 204")
    void delete_ok() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(patientService).delete(id);

        mockMvc.perform(delete("/api/patients/{id}", id).with(csrf()))
                .andExpect(status().isNoContent());

        verify(patientService).delete(id);
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("DELETE /api/patients/{id} → 403 si no es ADMIN")
    void delete_forbidden() throws Exception {
        mockMvc.perform(delete("/api/patients/{id}", UUID.randomUUID()).with(csrf()))
                .andExpect(status().isForbidden());
    }
}
