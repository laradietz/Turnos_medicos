package com.clinica.service;

import com.clinica.dto.PrescriptionDtos;
import com.clinica.entity.*;
import com.clinica.exception.*;
import com.clinica.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// ── PRESCRIPTION SERVICE ─────────────────────────────────────
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrescriptionService {

    private final PrescriptionRepository   prescriptionRepository;
    private final ConsultationRepository   consultationRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;

    public List<Prescription> findByPatient(UUID patientId) {
        ClinicalRecord cr = clinicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("ClinicalRecord for patient", patientId));
        return prescriptionRepository.findByClinicalRecordIdOrderByIssueDateDesc(cr.getId());
    }

    @Transactional
    public Prescription create(PrescriptionDtos.CreatePrescriptionRequest req) {
        Consultation consultation = consultationRepository.findById(req.consultationId())
                .orElseThrow(() -> new ResourceNotFoundException("Consultation", req.consultationId()));

        ClinicalRecord cr = consultation.getClinicalRecord();

        Prescription prescription = Prescription.builder()
                .consultation(consultation)
                .clinicalRecord(cr)
                .issueDate(LocalDate.now())
                .expirationDate(req.expirationDate())
                .notes(req.notes())
                .build();

        List<PrescriptionItem> items = req.items().stream().map(i -> {
            PrescriptionItem item = PrescriptionItem.builder()
                .medicationName(i.medicationName())
                .dosage(i.dosage())
                .frequency(i.frequency())
                .duration(i.duration())
                .instructions(i.instructions())
                .quantity(i.quantity())
                .prescription(prescription)
                .build();
            return item;
        }).toList();

        prescription.setItems(items);
        return prescriptionRepository.save(prescription);
    }
}
