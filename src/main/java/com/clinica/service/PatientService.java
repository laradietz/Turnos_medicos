package com.clinica.service;

import com.clinica.dto.PatientDtos;
import com.clinica.entity.*;
import com.clinica.exception.*;
import com.clinica.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientInsuranceRepository patientInsuranceRepository;
    private final HealthInsuranceRepository healthInsuranceRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll().stream()
                .filter(p -> p.getDeletedAt() == null).toList();
    }

    public Patient findById(UUID id) {
        return patientRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }

    public List<Patient> search(String name) {
        return patientRepository.searchByName(name);
    }

    @Transactional
    public Patient create(PatientDtos.CreatePatientRequest req) {
        if (patientRepository.existsByDniAndDeletedAtIsNull(req.dni())) {
            throw new BusinessException("Patient with DNI " + req.dni() + " already exists");
        }
        Patient patient = Patient.builder()
                .dni(req.dni())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .birthDate(req.birthDate())
                .gender(req.gender())
                .email(req.email())
                .phone(req.phone())
                .address(req.address())
                .city(req.city())
                .bloodType(req.bloodType())
                .emergencyContactName(req.emergencyContactName())
                .emergencyContactPhone(req.emergencyContactPhone())
                .active(true)
                .build();

        Patient saved = patientRepository.save(patient);

        // Crear historia clínica automáticamente
        ClinicalRecord record = ClinicalRecord.builder()
                .patient(saved)
                .build();
        clinicalRecordRepository.save(record);

        return saved;
    }

    @Transactional
    public Patient update(UUID id, PatientDtos.UpdatePatientRequest req) {
        Patient patient = findById(id);
        patient.setFirstName(req.firstName());
        patient.setLastName(req.lastName());
        patient.setEmail(req.email());
        patient.setPhone(req.phone());
        patient.setAddress(req.address());
        patient.setCity(req.city());
        patient.setBloodType(req.bloodType());
        patient.setEmergencyContactName(req.emergencyContactName());
        patient.setEmergencyContactPhone(req.emergencyContactPhone());
        return patientRepository.save(patient);
    }

    @Transactional
    public void delete(UUID id) {
        Patient patient = findById(id);
        patient.setDeletedAt(java.time.LocalDateTime.now());
        patientRepository.save(patient);
    }

    @Transactional
    public PatientInsurance addInsurance(UUID patientId,
                                          PatientDtos.PatientInsuranceRequest req) {
        Patient patient = findById(patientId);
        HealthInsurance hi = healthInsuranceRepository.findById(req.healthInsuranceId())
                .orElseThrow(() -> new ResourceNotFoundException("HealthInsurance", req.healthInsuranceId()));

        if (patientInsuranceRepository.existsByPatientIdAndHealthInsuranceId(
                patientId, req.healthInsuranceId())) {
            throw new BusinessException("Patient already has this health insurance");
        }

        PatientInsurance pi = PatientInsurance.builder()
                .patient(patient)
                .healthInsurance(hi)
                .affiliateNumber(req.affiliateNumber())
                .plan(req.plan())
                .expirationDate(req.expirationDate())
                .isPrimary(req.isPrimary() != null ? req.isPrimary() : false)
                .build();

        return patientInsuranceRepository.save(pi);
    }

    public List<PatientInsurance> findInsurances(UUID patientId) {
        findById(patientId); // valida existencia
        return patientInsuranceRepository.findAllByPatientId(patientId);
    }
}
