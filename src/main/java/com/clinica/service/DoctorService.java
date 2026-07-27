package com.clinica.service;

import com.clinica.dto.DoctorDtos;
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
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final UserRepository userRepository;

    public List<Doctor> findAll() {
        return doctorRepository.findAllByActiveTrueAndDeletedAtIsNull();
    }

    public Doctor findById(UUID id) {
        return doctorRepository.findById(id)
                .filter(d -> d.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
    }

    public List<Doctor> findBySpecialty(UUID specialtyId) {
        return doctorRepository.findBySpecialtyId(specialtyId);
    }

    @Transactional
    public Doctor create(DoctorDtos.CreateDoctorRequest req) {
        if (doctorRepository.existsByLicenseNumberAndDeletedAtIsNull(req.licenseNumber())) {
            throw new BusinessException("License number already registered: " + req.licenseNumber());
        }

        Specialty specialty = specialtyRepository.findById(req.specialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", req.specialtyId()));

        User user = null;
        if (req.userId() != null) {
            user = userRepository.findById(req.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", req.userId()));
        }

        Doctor doctor = Doctor.builder()
                .licenseNumber(req.licenseNumber())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .phone(req.phone())
                .biography(req.biography())
                .consultationFeeMinutes(req.consultationFeeMinutes())
                .specialty(specialty)
                .user(user)
                .active(true)
                .build();

        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor update(UUID id, DoctorDtos.UpdateDoctorRequest req) {
        Doctor doctor = findById(id);
        doctor.setFirstName(req.firstName());
        doctor.setLastName(req.lastName());
        doctor.setEmail(req.email());
        doctor.setPhone(req.phone());
        doctor.setBiography(req.biography());
        if (req.consultationFeeMinutes() != null) {
            doctor.setConsultationFeeMinutes(req.consultationFeeMinutes());
        }
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deactivate(UUID id) {
        Doctor doctor = findById(id);
        doctor.setActive(false);
        doctorRepository.save(doctor);
    }

    @Transactional
    public void delete(UUID id) {
        Doctor doctor = findById(id);
        doctor.setDeletedAt(java.time.LocalDateTime.now());
        doctorRepository.save(doctor);
    }
}
