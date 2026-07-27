package com.clinica.mapper;

import com.clinica.dto.PatientDtos;
import com.clinica.entity.HealthInsurance;
import com.clinica.entity.Patient;
import com.clinica.entity.PatientInsurance;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-24T23:11:32-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class PatientMapperImpl implements PatientMapper {

    @Override
    public PatientDtos.PatientResponse toResponse(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        UUID id = null;
        String dni = null;
        String firstName = null;
        String lastName = null;
        LocalDate birthDate = null;
        String email = null;
        String phone = null;
        String address = null;
        String city = null;
        Boolean active = null;
        LocalDateTime createdAt = null;

        id = patient.getId();
        dni = patient.getDni();
        firstName = patient.getFirstName();
        lastName = patient.getLastName();
        birthDate = patient.getBirthDate();
        email = patient.getEmail();
        phone = patient.getPhone();
        address = patient.getAddress();
        city = patient.getCity();
        active = patient.getActive();
        createdAt = patient.getCreatedAt();

        String gender = patient.getGender() != null ? patient.getGender().name() : null;
        String bloodType = patient.getBloodType() != null ? patient.getBloodType().name() : null;

        PatientDtos.PatientResponse patientResponse = new PatientDtos.PatientResponse( id, dni, firstName, lastName, birthDate, gender, email, phone, address, city, bloodType, active, createdAt );

        return patientResponse;
    }

    @Override
    public List<PatientDtos.PatientResponse> toResponseList(List<Patient> patients) {
        if ( patients == null ) {
            return null;
        }

        List<PatientDtos.PatientResponse> list = new ArrayList<PatientDtos.PatientResponse>( patients.size() );
        for ( Patient patient : patients ) {
            list.add( toResponse( patient ) );
        }

        return list;
    }

    @Override
    public Patient toEntity(PatientDtos.CreatePatientRequest request) {
        if ( request == null ) {
            return null;
        }

        Patient.PatientBuilder<?, ?> patient = Patient.builder();

        patient.dni( request.dni() );
        patient.firstName( request.firstName() );
        patient.lastName( request.lastName() );
        patient.birthDate( request.birthDate() );
        patient.gender( request.gender() );
        patient.email( request.email() );
        patient.phone( request.phone() );
        patient.address( request.address() );
        patient.city( request.city() );
        patient.bloodType( request.bloodType() );
        patient.emergencyContactName( request.emergencyContactName() );
        patient.emergencyContactPhone( request.emergencyContactPhone() );

        return patient.build();
    }

    @Override
    public void updateFromRequest(PatientDtos.UpdatePatientRequest request, Patient patient) {
        if ( request == null ) {
            return;
        }

        if ( request.firstName() != null ) {
            patient.setFirstName( request.firstName() );
        }
        if ( request.lastName() != null ) {
            patient.setLastName( request.lastName() );
        }
        if ( request.email() != null ) {
            patient.setEmail( request.email() );
        }
        if ( request.phone() != null ) {
            patient.setPhone( request.phone() );
        }
        if ( request.address() != null ) {
            patient.setAddress( request.address() );
        }
        if ( request.city() != null ) {
            patient.setCity( request.city() );
        }
        if ( request.bloodType() != null ) {
            patient.setBloodType( request.bloodType() );
        }
        if ( request.emergencyContactName() != null ) {
            patient.setEmergencyContactName( request.emergencyContactName() );
        }
        if ( request.emergencyContactPhone() != null ) {
            patient.setEmergencyContactPhone( request.emergencyContactPhone() );
        }
    }

    @Override
    public PatientDtos.PatientInsuranceResponse toInsuranceResponse(PatientInsurance pi) {
        if ( pi == null ) {
            return null;
        }

        String healthInsuranceName = null;
        UUID id = null;
        String affiliateNumber = null;
        String plan = null;
        LocalDate expirationDate = null;
        Boolean isPrimary = null;

        healthInsuranceName = piHealthInsuranceName( pi );
        id = pi.getId();
        affiliateNumber = pi.getAffiliateNumber();
        plan = pi.getPlan();
        expirationDate = pi.getExpirationDate();
        isPrimary = pi.getIsPrimary();

        PatientDtos.PatientInsuranceResponse patientInsuranceResponse = new PatientDtos.PatientInsuranceResponse( id, healthInsuranceName, affiliateNumber, plan, expirationDate, isPrimary );

        return patientInsuranceResponse;
    }

    @Override
    public List<PatientDtos.PatientInsuranceResponse> toInsuranceResponseList(List<PatientInsurance> list) {
        if ( list == null ) {
            return null;
        }

        List<PatientDtos.PatientInsuranceResponse> list1 = new ArrayList<PatientDtos.PatientInsuranceResponse>( list.size() );
        for ( PatientInsurance patientInsurance : list ) {
            list1.add( toInsuranceResponse( patientInsurance ) );
        }

        return list1;
    }

    private String piHealthInsuranceName(PatientInsurance patientInsurance) {
        if ( patientInsurance == null ) {
            return null;
        }
        HealthInsurance healthInsurance = patientInsurance.getHealthInsurance();
        if ( healthInsurance == null ) {
            return null;
        }
        String name = healthInsurance.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
