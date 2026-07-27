package com.clinica.service;

import com.clinica.dto.AvailabilityDtos;
import com.clinica.entity.*;
import com.clinica.exception.*;
import com.clinica.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final DoctorRepository       doctorRepository;
    private final ScheduleRepository     scheduleRepository;
    private final AppointmentRepository  appointmentRepository;

    public List<Availability> findByDoctorAndRange(UUID doctorId, LocalDate from, LocalDate to) {
        return availabilityRepository.findByDoctorAndDateRange(doctorId, from, to);
    }

    @Transactional
    public Availability create(UUID doctorId, AvailabilityDtos.CreateAvailabilityRequest req) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));

        if (req.startTime() != null && req.endTime() != null
                && !req.endTime().isAfter(req.startTime())) {
            throw new BusinessException("end_time must be after start_time");
        }

        Availability av = Availability.builder()
                .date(req.date())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .type(req.type())
                .reason(req.reason())
                .doctor(doctor)
                .build();

        return availabilityRepository.save(av);
    }

    /**
     * Calcula los slots libres de un médico en una fecha,
     * cruzando agenda semanal con turnos ya reservados y bloqueos.
     */
    public List<AvailabilityDtos.TimeSlotResponse> getAvailableSlots(UUID doctorId, LocalDate date) {
        List<Schedule> schedules = scheduleRepository
                .findActiveByDoctorAndDate(doctorId, date);

        List<Availability> blocks = availabilityRepository
                .findByDoctorAndDateRange(doctorId, date, date)
                .stream()
                .filter(a -> a.getType() != Availability.AvailabilityType.AVAILABLE)
                .toList();

        List<Appointment> existing = appointmentRepository
                .findByDoctorAndDate(doctorId, date)
                .stream()
                .filter(a -> a.getStatus() != Appointment.AppointmentStatus.CANCELLED)
                .toList();

        List<AvailabilityDtos.TimeSlotResponse> slots = new ArrayList<>();

        for (Schedule s : schedules) {
            if (!s.getDayOfWeek().equals(date.getDayOfWeek())) continue;

            LocalTime cursor = s.getStartTime();
            int minutes = s.getSlotDurationMinutes();

            while (cursor.plusMinutes(minutes).compareTo(s.getEndTime()) <= 0) {
                LocalTime slotEnd = cursor.plusMinutes(minutes);
                boolean blocked = isBlocked(cursor, slotEnd, blocks);
                boolean taken   = isTaken(cursor, existing);
                slots.add(new AvailabilityDtos.TimeSlotResponse(cursor, slotEnd, !blocked && !taken));
                cursor = slotEnd;
            }
        }
        return slots;
    }

    private boolean isBlocked(LocalTime start, LocalTime end, List<Availability> blocks) {
        for (Availability b : blocks) {
            if (b.getStartTime() == null) return true;
            if (start.isBefore(b.getEndTime()) && end.isAfter(b.getStartTime())) return true;
        }
        return false;
    }

    private boolean isTaken(LocalTime start, List<Appointment> existing) {
        return existing.stream().anyMatch(a -> a.getStartTime().equals(start));
    }
}
