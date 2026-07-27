package com.clinica.service;

import com.clinica.dto.AuthDtos;
import com.clinica.dto.DoctorDtos;
import com.clinica.entity.*;
import com.clinica.exception.BusinessException;
import com.clinica.repository.*;
import com.clinica.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ── AUTH SERVICE TEST ────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock AuthenticationManager authManager;
    @Mock JwtUtil               jwtUtil;
    @InjectMocks AuthService    authService;

    @Test
    @DisplayName("Login exitoso retorna token")
    void login_ok() {
        var userDetails = new User("recepcionista", "hash",
                List.of(new SimpleGrantedAuthority("ROLE_RECEPCIONISTA")));
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authManager.authenticate(any())).thenReturn(auth);
        when(jwtUtil.generateToken("recepcionista", "RECEPCIONISTA")).thenReturn("jwt.token.here");

        var req = new AuthDtos.LoginRequest("recepcionista", "pass123");
        var response = authService.login(req);

        assertThat(response.token()).isEqualTo("jwt.token.here");
        assertThat(response.role()).isEqualTo("RECEPCIONISTA");
    }

    @Test
    @DisplayName("Credenciales incorrectas lanza BusinessException")
    void login_badCredentials_throws() {
        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(
                new AuthDtos.LoginRequest("user", "wrong")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid username or password");
    }
}

// ── DOCTOR SERVICE TEST ──────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock DoctorRepository   doctorRepository;
    @Mock SpecialtyRepository specialtyRepository;
    @Mock UserRepository     userRepository;
    @InjectMocks DoctorService doctorService;

    private DoctorDtos.CreateDoctorRequest buildRequest(UUID specialtyId) {
        return new DoctorDtos.CreateDoctorRequest(
                "MP-12345", "Laura", "Ramírez",
                "laura@clinica.com", "1144556677",
                "Médica clínica con 10 años de experiencia",
                30, specialtyId, null
        );
    }

    @Test
    @DisplayName("Crear médico exitosamente")
    void create_ok() {
        UUID specId = UUID.randomUUID();
        Specialty spec = Specialty.builder().name("Clínica Médica").active(true).build();

        when(doctorRepository.existsByLicenseNumberAndDeletedAtIsNull("MP-12345")).thenReturn(false);
        when(specialtyRepository.findById(specId)).thenReturn(Optional.of(spec));
        when(doctorRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Doctor result = doctorService.create(buildRequest(specId));

        assertThat(result.getLicenseNumber()).isEqualTo("MP-12345");
        assertThat(result.getFirstName()).isEqualTo("Laura");
        assertThat(result.getActive()).isTrue();
        assertThat(result.getSpecialty()).isEqualTo(spec);
    }

    @Test
    @DisplayName("Matrícula duplicada lanza BusinessException")
    void create_duplicateLicense_throws() {
        when(doctorRepository.existsByLicenseNumberAndDeletedAtIsNull("MP-12345")).thenReturn(true);

        assertThatThrownBy(() -> doctorService.create(buildRequest(UUID.randomUUID())))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("MP-12345");
    }

    @Test
    @DisplayName("Desactivar médico setea active = false")
    void deactivate_ok() {
        UUID id = UUID.randomUUID();
        Doctor doctor = Doctor.builder()
                .licenseNumber("MP-12345").firstName("Laura").lastName("Ramírez")
                .consultationFeeMinutes(30).active(true).build();

        when(doctorRepository.findById(id)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        doctorService.deactivate(id);

        assertThat(doctor.getActive()).isFalse();
    }
}
