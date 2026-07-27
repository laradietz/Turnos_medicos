package com.clinica.service;

import com.clinica.entity.*;
import com.clinica.exception.*;
import com.clinica.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// ── CLINICAL RECORD SERVICE ──────────────────────────────────
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClinicalRecordService {

    private final ClinicalRecordRepository clinicalRecordRepository;
    private final PatientRepository        patientRepository;

    public ClinicalRecord findByPatient(UUID patientId) {
        patientRepository.findById(patientId)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
        return clinicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("ClinicalRecord for patient", patientId));
    }

    @Transactional
    public ClinicalRecord updateNotes(UUID patientId, String allergies,
                                       String chronicConditions, String currentMedications,
                                       String familyHistory, String surgicalHistory, String notes) {
        ClinicalRecord record = findByPatient(patientId);
        if (allergies         != null) record.setAllergies(allergies);
        if (chronicConditions != null) record.setChronicConditions(chronicConditions);
        if (currentMedications!= null) record.setCurrentMedications(currentMedications);
        if (familyHistory     != null) record.setFamilyHistory(familyHistory);
        if (surgicalHistory   != null) record.setSurgicalHistory(surgicalHistory);
        if (notes             != null) record.setNotes(notes);
        return clinicalRecordRepository.save(record);
    }
}
