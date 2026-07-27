package com.clinica.service;

import com.clinica.dto.AuthDtos;
import com.clinica.exception.BusinessException;
import com.clinica.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(), request.password()));

            UserDetails user = (UserDetails) auth.getPrincipal();
            String role = user.getAuthorities().iterator().next()
                    .getAuthority().replace("ROLE_", "");

            String token = jwtUtil.generateToken(user.getUsername(), role);

            return new AuthDtos.LoginResponse(token, user.getUsername(), role, 86400000L);

        } catch (BadCredentialsException e) {
            throw new BusinessException("Invalid username or password");
        }
    }
}
