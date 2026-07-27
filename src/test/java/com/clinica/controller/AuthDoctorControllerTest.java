package com.clinica.controller;

import com.clinica.dto.AuthDtos;
import com.clinica.dto.DoctorDtos;
import com.clinica.entity.*;
import com.clinica.exception.BusinessException;
import com.clinica.security.JwtAuthenticationFilter;
import com.clinica.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ── AUTH CONTROLLER TEST ─────────────────────────────────────
@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class
    )
)
class AuthControllerTest {

    @Autowired MockMvc      mockMvc;
    @MockBean  AuthService  authService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("POST /api/auth/login → 200 con token")
    void login_ok() throws Exception {
        when(authService.login(any())).thenReturn(
                new AuthDtos.LoginResponse("jwt.token", "admin", "ADMIN", 86400000L));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new AuthDtos.LoginRequest("admin", "1234"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/auth/login → 422 credenciales incorrectas")
    void login_badCredentials() throws Exception {
        when(authService.login(any()))
                .thenThrow(new BusinessException("Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new AuthDtos.LoginRequest("admin", "wrong"))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /api/auth/login → 400 si faltan campos")
    void login_missingFields() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new AuthDtos.LoginRequest("", ""))))
                .andExpect(status().isBadRequest());
    }
}

// ── DOCTOR CONTROLLER TEST ───────────────────────────────────
@WebMvcTest(
    controllers = DoctorController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class
    )
)
class DoctorControllerTest {

    @Autowired MockMvc       mockMvc;
    @MockBean  DoctorService doctorService;

    private final ObjectMapper mapper = new ObjectMapper();

    private Doctor buildDoctor() {
        Specialty spec = Specialty.builder().name("Clínica Médica").active(true).build();
        return Doctor.builder()
                .licenseNumber("MP-99999")
                .firstName("Laura").lastName("Ramírez")
                .email("laura@clinica.com")
                .consultationFeeMinutes(30)
                .active(true)
                .specialty(spec)
                .build();
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("GET /api/doctors → 200 con lista")
    void findAll_ok() throws Exception {
        when(doctorService.findAll()).thenReturn(List.of(buildDoctor()));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].licenseNumber").value("MP-99999"))
                .andExpect(jsonPath("$[0].firstName").value("Laura"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/doctors → 201 con médico creado")
    void create_ok() throws Exception {
        var req = new DoctorDtos.CreateDoctorRequest(
                "MP-99999", "Laura", "Ramírez",
                "laura@clinica.com", "1155443322",
                "Médica clínica", 30,
                UUID.randomUUID(), null
        );
        when(doctorService.create(any())).thenReturn(buildDoctor());

        mockMvc.perform(post("/api/doctors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licenseNumber").value("MP-99999"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    @DisplayName("POST /api/doctors → 403 si no es ADMIN")
    void create_forbidden() throws Exception {
        var req = new DoctorDtos.CreateDoctorRequest(
                "MP-99999", "Laura", "Ramírez",
                null, null, null, 30, UUID.randomUUID(), null
        );

        mockMvc.perform(post("/api/doctors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/doctors/{id}/deactivate → 204")
    void deactivate_ok() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(doctorService).deactivate(id);

        mockMvc.perform(patch("/api/doctors/{id}/deactivate", id).with(csrf()))
                .andExpect(status().isNoContent());

        verify(doctorService).deactivate(id);
    }
}
