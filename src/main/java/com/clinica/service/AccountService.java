package com.clinica.service;

import com.clinica.dto.UserDtos;
import com.clinica.entity.User;
import com.clinica.exception.BusinessException;
import com.clinica.exception.ResourceNotFoundException;
import com.clinica.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ── ACCOUNT SERVICE (self-service: "mi perfil") ──────────────
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDtos.UserResponse getMyAccount(String username) {
        User user = findByUsername(username);
        return toResponse(user);
    }

    @Transactional
    public UserDtos.UserResponse updateMyAccount(String username,
                                                  UserDtos.UpdateMyAccountRequest req) {
        User user = findByUsername(username);

        if (!passwordEncoder.matches(req.currentPassword(), user.getPasswordHash())) {
            throw new BusinessException("La contraseña actual no es correcta");
        }

        if (req.newUsername() != null && !req.newUsername().isBlank()
                && !req.newUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndDeletedAtIsNull(req.newUsername())) {
                throw new BusinessException("Ese nombre de usuario ya está en uso");
            }
            user.setUsername(req.newUsername());
        }

        if (req.newPassword() != null && !req.newPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    private User findByUsername(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
    }

    private UserDtos.UserResponse toResponse(User u) {
        return new UserDtos.UserResponse(
                u.getId(), u.getUsername(), u.getEmail(), u.getFirstName(), u.getLastName(),
                u.getPhone(), u.getActive(), u.getRole().getName(), u.getCreatedAt());
    }
}
