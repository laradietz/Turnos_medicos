package com.clinica.service;

import com.clinica.dto.AppointmentDtos;
import com.clinica.entity.*;
import com.clinica.exception.*;
import com.clinica.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository      appointmentRepository;
    private final PatientRepository          patientRepository;
    private final DoctorRepository           doctorRepository;
    private final OfficeRepository           officeRepository;
    private final HealthInsuranceRepository  healthInsuranceRepository;
    private final AppointmentStatusHistoryRepository statusHistoryRepository;
    private final SystemConfigurationRepository configRepository;

    public Appointment findById(UUID id) {
        return appointmentRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    public List<Appointment> findByDoctor(UUID doctorId, java.time.LocalDate date) {
        return appointmentRepository.findByDoctorAndDate(doctorId, date);
    }

    public List<Appointment> findByDoctorAndRange(UUID doctorId, java.time.LocalDate from,
                                                   java.time.LocalDate to) {
        return appointmentRepository.findByDoctorAndDateRange(doctorId, from, to);
    }

    public List<Appointment> findByPatient(UUID patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Transactional
    public Appointment create(AppointmentDtos.CreateAppointmentRequest req) {
        Patient patient = patientRepository.findById(req.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", req.patientId()));

        Doctor doctor = doctorRepository.findById(req.doctorId())
                .filter(d -> d.getDeletedAt() == null && d.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", req.doctorId()));

        // Verificar que no existe otro turno en el mismo horario
        appointmentRepository.findConflict(req.doctorId(), req.appointmentDate(), req.startTime())
                .ifPresent(a -> {
                    throw new BusinessException("Doctor already has an appointment at that time");
                });

        // Verificar límite diario desde configuración
        int maxPerDay = configRepository.findByConfigKey("max_appointments_per_day")
                .map(c -> Integer.parseInt(c.getConfigValue()))
                .orElse(20);

        long count = appointmentRepository.countByDoctorAndDate(req.doctorId(), req.appointmentDate());
        if (count >= maxPerDay) {
            throw new BusinessException("Doctor has reached the maximum appointments for that day");
        }

        Office office = req.officeId() != null
                ? officeRepository.findById(req.officeId()).orElse(null)
                : null;

        HealthInsurance hi = req.healthInsuranceId() != null
                ? healthInsuranceRepository.findById(req.healthInsuranceId()).orElse(null)
                : null;

        java.time.LocalTime endTime = req.startTime()
                .plusMinutes(doctor.getConsultationFeeMinutes());

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .office(office)
                .healthInsurance(hi)
                .appointmentDate(req.appointmentDate())
                .startTime(req.startTime())
                .endTime(endTime)
                .type(req.type())
                .status(Appointment.AppointmentStatus.PENDING)
                .notes(req.notes())
                .reminderSent(false)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        saveStatusHistory(saved, null, Appointment.AppointmentStatus.PENDING, "Created");
        return saved;
    }

    @Transactional
    public Appointment confirm(UUID id) {
        Appointment a = findById(id);
        validateTransition(a.getStatus(), Appointment.AppointmentStatus.CONFIRMED);
        Appointment.AppointmentStatus prev = a.getStatus();
        a.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        Appointment saved = appointmentRepository.save(a);
        saveStatusHistory(saved, prev, Appointment.AppointmentStatus.CONFIRMED, "Confirmed");
        return saved;
    }

    @Transactional
    public Appointment cancel(UUID id, AppointmentDtos.CancelAppointmentRequest req) {
        Appointment a = findById(id);
        validateTransition(a.getStatus(), Appointment.AppointmentStatus.CANCELLED);
        Appointment.AppointmentStatus prev = a.getStatus();
        a.setStatus(Appointment.AppointmentStatus.CANCELLED);
        a.setCancellationReason(req.cancellationReason());
        Appointment saved = appointmentRepository.save(a);
        saveStatusHistory(saved, prev, Appointment.AppointmentStatus.CANCELLED, req.cancellationReason());
        return saved;
    }

    @Transactional
    public Appointment complete(UUID id) {
        Appointment a = findById(id);
        validateTransition(a.getStatus(), Appointment.AppointmentStatus.COMPLETED);
        Appointment.AppointmentStatus prev = a.getStatus();
        a.setStatus(Appointment.AppointmentStatus.COMPLETED);
        Appointment saved = appointmentRepository.save(a);
        saveStatusHistory(saved, prev, Appointment.AppointmentStatus.COMPLETED, "Completed");
        return saved;
    }

    @Transactional
    public Appointment markNoShow(UUID id) {
        Appointment a = findById(id);
        validateTransition(a.getStatus(), Appointment.AppointmentStatus.NO_SHOW);
        Appointment.AppointmentStatus prev = a.getStatus();
        a.setStatus(Appointment.AppointmentStatus.NO_SHOW);
        Appointment saved = appointmentRepository.save(a);
        saveStatusHistory(saved, prev, Appointment.AppointmentStatus.NO_SHOW, "Patient did not show up");
        return saved;
    }

    private void validateTransition(Appointment.AppointmentStatus from,
                                     Appointment.AppointmentStatus to) {
        boolean valid = switch (to) {
            case CONFIRMED  -> from == Appointment.AppointmentStatus.PENDING;
            case CANCELLED  -> from == Appointment.AppointmentStatus.PENDING
                            || from == Appointment.AppointmentStatus.CONFIRMED;
            case COMPLETED  -> from == Appointment.AppointmentStatus.CONFIRMED;
            case NO_SHOW    -> from == Appointment.AppointmentStatus.CONFIRMED;
            default         -> false;
        };
        if (!valid) {
            throw new BusinessException(
                    "Invalid status transition from " + from + " to " + to);
        }
    }

    private void saveStatusHistory(Appointment appointment,
                                    Appointment.AppointmentStatus prev,
                                    Appointment.AppointmentStatus next,
                                    String reason) {
        AppointmentStatusHistory h = AppointmentStatusHistory.builder()
                .appointment(appointment)
                .previousStatus(prev)
                .newStatus(next)
                .reason(reason)
                .build();
        statusHistoryRepository.save(h);
    }
}
