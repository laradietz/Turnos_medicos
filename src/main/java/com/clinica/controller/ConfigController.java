package com.clinica.controller;

import com.clinica.entity.*;
import com.clinica.exception.ResourceNotFoundException;
import com.clinica.repository.SystemConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// ── CONFIGURACIÓN DEL SISTEMA ────────────────────────────────
@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
public class ConfigController {

    private final SystemConfigurationRepository configRepository;

    /** GET /api/configuracion */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SystemConfiguration>> findAll() {
        return ResponseEntity.ok(configRepository.findAll());
    }

    /** GET /api/configuracion/{key} */
    @GetMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemConfiguration> findByKey(@PathVariable String key) {
        return ResponseEntity.ok(
                configRepository.findByConfigKey(key)
                        .orElseThrow(() -> new ResourceNotFoundException("ConfigKey", key))
        );
    }

    /**
     * PUT /api/configuracion/{key}
     * Body: { "value": "nuevo_valor" }
     */
    @PutMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemConfiguration> update(
            @PathVariable String key,
            @RequestBody Map<String, String> body) {
        SystemConfiguration config = configRepository.findByConfigKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("ConfigKey", key));
        if (!config.getEditable()) {
            throw new com.clinica.exception.BusinessException("Esta configuración no es editable");
        }
        config.setConfigValue(body.get("value"));
        return ResponseEntity.ok(configRepository.save(config));
    }
}
