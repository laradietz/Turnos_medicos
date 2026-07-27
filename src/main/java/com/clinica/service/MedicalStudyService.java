package com.clinica.service;

import com.clinica.dto.MedicalStudyDtos;
import com.clinica.entity.*;
import com.clinica.exception.*;
import com.clinica.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// ── MEDICAL STUDY SERVICE ────────────────────────────────────
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalStudyService {

    private final MedicalStudyRepository   studyRepository;
    private final ConsultationRepository   consultationRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;

    public List<MedicalStudy> findByPatient(UUID patientId) {
        ClinicalRecord cr = clinicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("ClinicalRecord for patient", patientId));
        return studyRepository.findByClinicalRecordIdOrderByOrderedDateDesc(cr.getId());
    }

    @Transactional
    public MedicalStudy create(MedicalStudyDtos.CreateMedicalStudyRequest req) {
        Consultation consultation = consultationRepository.findById(req.consultationId())
                .orElseThrow(() -> new ResourceNotFoundException("Consultation", req.consultationId()));

        MedicalStudy study = MedicalStudy.builder()
                .consultation(consultation)
                .clinicalRecord(consultation.getClinicalRecord())
                .name(req.name())
                .studyType(req.studyType())
                .orderedDate(LocalDate.now())
                .status(MedicalStudy.StudyStatus.ORDERED)
                .notes(req.notes())
                .build();

        return studyRepository.save(study);
    }

    @Transactional
    public MedicalStudy updateResult(UUID id, MedicalStudyDtos.UpdateStudyResultRequest req) {
        MedicalStudy study = studyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalStudy", id));
        study.setResult(req.result());
        study.setFileUrl(req.fileUrl());
        study.setResultDate(req.resultDate() != null ? req.resultDate() : LocalDate.now());
        study.setStatus(MedicalStudy.StudyStatus.COMPLETED);
        return studyRepository.save(study);
    }
}
