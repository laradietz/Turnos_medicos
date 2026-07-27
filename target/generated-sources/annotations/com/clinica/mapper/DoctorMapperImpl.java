package com.clinica.mapper;

import com.clinica.dto.DoctorDtos;
import com.clinica.dto.SpecialtyDtos;
import com.clinica.entity.Doctor;
import com.clinica.entity.Specialty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-24T23:11:31-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class DoctorMapperImpl implements DoctorMapper {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Override
    public DoctorDtos.DoctorResponse toResponse(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        SpecialtyDtos.SpecialtyResponse specialty = null;
        UUID id = null;
        String licenseNumber = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        String phone = null;
        String biography = null;
        Integer consultationFeeMinutes = null;
        Boolean active = null;

        specialty = specialtyMapper.toResponse( doctor.getSpecialty() );
        id = doctor.getId();
        licenseNumber = doctor.getLicenseNumber();
        firstName = doctor.getFirstName();
        lastName = doctor.getLastName();
        email = doctor.getEmail();
        phone = doctor.getPhone();
        biography = doctor.getBiography();
        consultationFeeMinutes = doctor.getConsultationFeeMinutes();
        active = doctor.getActive();

        DoctorDtos.DoctorResponse doctorResponse = new DoctorDtos.DoctorResponse( id, licenseNumber, firstName, lastName, email, phone, biography, consultationFeeMinutes, active, specialty );

        return doctorResponse;
    }

    @Override
    public DoctorDtos.DoctorSummaryResponse toSummary(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        String specialtyName = null;
        UUID id = null;
        Boolean active = null;

        specialtyName = doctorSpecialtyName( doctor );
        id = doctor.getId();
        active = doctor.getActive();

        String fullName = doctor.getFirstName() + ' ' + doctor.getLastName();

        DoctorDtos.DoctorSummaryResponse doctorSummaryResponse = new DoctorDtos.DoctorSummaryResponse( id, fullName, specialtyName, active );

        return doctorSummaryResponse;
    }

    @Override
    public List<DoctorDtos.DoctorResponse> toResponseList(List<Doctor> doctors) {
        if ( doctors == null ) {
            return null;
        }

        List<DoctorDtos.DoctorResponse> list = new ArrayList<DoctorDtos.DoctorResponse>( doctors.size() );
        for ( Doctor doctor : doctors ) {
            list.add( toResponse( doctor ) );
        }

        return list;
    }

    @Override
    public List<DoctorDtos.DoctorSummaryResponse> toSummaryList(List<Doctor> doctors) {
        if ( doctors == null ) {
            return null;
        }

        List<DoctorDtos.DoctorSummaryResponse> list = new ArrayList<DoctorDtos.DoctorSummaryResponse>( doctors.size() );
        for ( Doctor doctor : doctors ) {
            list.add( toSummary( doctor ) );
        }

        return list;
    }

    @Override
    public Doctor toEntity(DoctorDtos.CreateDoctorRequest request) {
        if ( request == null ) {
            return null;
        }

        Doctor.DoctorBuilder<?, ?> doctor = Doctor.builder();

        doctor.licenseNumber( request.licenseNumber() );
        doctor.firstName( request.firstName() );
        doctor.lastName( request.lastName() );
        doctor.email( request.email() );
        doctor.phone( request.phone() );
        doctor.biography( request.biography() );
        doctor.consultationFeeMinutes( request.consultationFeeMinutes() );

        return doctor.build();
    }

    @Override
    public void updateFromRequest(DoctorDtos.UpdateDoctorRequest request, Doctor doctor) {
        if ( request == null ) {
            return;
        }

        if ( request.firstName() != null ) {
            doctor.setFirstName( request.firstName() );
        }
        if ( request.lastName() != null ) {
            doctor.setLastName( request.lastName() );
        }
        if ( request.email() != null ) {
            doctor.setEmail( request.email() );
        }
        if ( request.phone() != null ) {
            doctor.setPhone( request.phone() );
        }
        if ( request.biography() != null ) {
            doctor.setBiography( request.biography() );
        }
        if ( request.consultationFeeMinutes() != null ) {
            doctor.setConsultationFeeMinutes( request.consultationFeeMinutes() );
        }
    }

    private String doctorSpecialtyName(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }
        Specialty specialty = doctor.getSpecialty();
        if ( specialty == null ) {
            return null;
        }
        String name = specialty.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
