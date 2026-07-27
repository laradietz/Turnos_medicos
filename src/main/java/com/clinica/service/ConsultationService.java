package com.clinica.service;

import com.clinica.dto.ConsultationDtos;
import com.clinica.entity.*;
import com.clinica.exception.*;
import com.clinica.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

// ── CONSULTATION SERVICE ─────────────────────────────────────
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultationService {

    private final ConsultationRepository   consultationRepository;
    private final AppointmentRepository    appointmentRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;

    public List<Consultation> findByPatient(UUID patientId) {
        ClinicalRecord cr = clinicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("ClinicalRecord for patient", patientId));
        return consultationRepository.findByClinicalRecordIdOrderByCreatedAtDesc(cr.getId());
    }

    public Consultation findById(UUID id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation", id));
    }

    @Transactional
    public Consultation create(ConsultationDtos.CreateConsultationRequest req) {
        Appointment appointment = appointmentRepository.findById(req.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", req.appointmentId()));

        if (appointment.getStatus() != Appointment.AppointmentStatus.CONFIRMED
                && appointment.getStatus() != Appointment.AppointmentStatus.COMPLETED) {
            throw new BusinessException("Appointment must be CONFIRMED or COMPLETED to create a consultation");
        }

        consultationRepository.findByAppointmentId(req.appointmentId()).ifPresent(c -> {
            throw new BusinessException("Consultation already exists for this appointment");
        });

        ClinicalRecord cr = clinicalRecordRepository
                .findByPatientId(appointment.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ClinicalRecord for patient", appointment.getPatient().getId()));

        Consultation consultation = Consultation.builder()
                .appointment(appointment)
                .clinicalRecord(cr)
                .reason(req.reason())
                .symptoms(req.symptoms())
                .diagnosis(req.diagnosis())
                .treatment(req.treatment())
                .observations(req.observations())
                .weightKg(req.weightKg())
                .heightCm(req.heightCm())
                .bloodPressure(req.bloodPressure())
                .temperature(req.temperature())
                .heartRate(req.heartRate())
                .build();

        // Marcar turno como completado
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        return consultationRepository.save(consultation);
    }
}
