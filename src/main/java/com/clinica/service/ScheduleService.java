package com.clinica.service;

import com.clinica.dto.ScheduleDtos;
import com.clinica.entity.*;
import com.clinica.exception.*;
import com.clinica.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;

    public List<Schedule> findByDoctor(UUID doctorId) {
        return scheduleRepository.findActiveByDoctorAndDate(doctorId, LocalDate.now());
    }

    @Transactional
    public Schedule create(UUID doctorId, ScheduleDtos.CreateScheduleRequest req) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));

        if (req.endTime().isBefore(req.startTime()) || req.endTime().equals(req.startTime())) {
            throw new BusinessException("end_time must be after start_time");
        }
        if (req.validUntil() != null && req.validUntil().isBefore(req.validFrom())) {
            throw new BusinessException("valid_until must be after valid_from");
        }

        Office office = null;
        if (req.officeId() != null) {
            office = officeRepository.findById(req.officeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Office", req.officeId()));
        }

        Schedule schedule = Schedule.builder()
                .dayOfWeek(req.dayOfWeek())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .slotDurationMinutes(req.slotDurationMinutes())
                .validFrom(req.validFrom())
                .validUntil(req.validUntil())
                .active(true)
                .doctor(doctor)
                .office(office)
                .build();

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deactivate(UUID scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", scheduleId));
        schedule.setActive(false);
        scheduleRepository.save(schedule);
    }
}
