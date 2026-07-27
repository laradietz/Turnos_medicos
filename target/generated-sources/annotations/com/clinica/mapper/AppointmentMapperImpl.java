package com.clinica.mapper;

import com.clinica.dto.AppointmentDtos;
import com.clinica.dto.DoctorDtos;
import com.clinica.dto.PatientDtos;
import com.clinica.entity.Appointment;
import com.clinica.entity.Office;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class AppointmentMapperImpl implements AppointmentMapper {

    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public AppointmentDtos.AppointmentResponse toResponse(Appointment a) {
        if ( a == null ) {
            return null;
        }

        PatientDtos.PatientResponse patient = null;
        DoctorDtos.DoctorSummaryResponse doctor = null;
        String officeNumber = null;
        UUID id = null;
        LocalDate appointmentDate = null;
        LocalTime startTime = null;
        LocalTime endTime = null;
        String notes = null;
        String cancellationReason = null;
        LocalDateTime createdAt = null;

        patient = patientMapper.toResponse( a.getPatient() );
        doctor = doctorMapper.toSummary( a.getDoctor() );
        officeNumber = aOfficeNumber( a );
        id = a.getId();
        appointmentDate = a.getAppointmentDate();
        startTime = a.getStartTime();
        endTime = a.getEndTime();
        notes = a.getNotes();
        cancellationReason = a.getCancellationReason();
        createdAt = a.getCreatedAt();

        String status = a.getStatus().name();
        String type = a.getType().name();

        AppointmentDtos.AppointmentResponse appointmentResponse = new AppointmentDtos.AppointmentResponse( id, appointmentDate, startTime, endTime, status, type, notes, cancellationReason, patient, doctor, officeNumber, createdAt );

        return appointmentResponse;
    }

    @Override
    public AppointmentDtos.AppointmentSummaryResponse toSummary(Appointment a) {
        if ( a == null ) {
            return null;
        }

        UUID id = null;
        LocalDate appointmentDate = null;
        LocalTime startTime = null;

        id = a.getId();
        appointmentDate = a.getAppointmentDate();
        startTime = a.getStartTime();

        String status = a.getStatus().name();
        String patientFullName = a.getPatient().getFirstName() + ' ' + a.getPatient().getLastName();
        String doctorFullName = a.getDoctor().getFirstName() + ' ' + a.getDoctor().getLastName();
        String type = a.getType().name();

        AppointmentDtos.AppointmentSummaryResponse appointmentSummaryResponse = new AppointmentDtos.AppointmentSummaryResponse( id, appointmentDate, startTime, status, patientFullName, doctorFullName, type );

        return appointmentSummaryResponse;
    }

    @Override
    public List<AppointmentDtos.AppointmentResponse> toResponseList(List<Appointment> list) {
        if ( list == null ) {
            return null;
        }

        List<AppointmentDtos.AppointmentResponse> list1 = new ArrayList<AppointmentDtos.AppointmentResponse>( list.size() );
        for ( Appointment appointment : list ) {
            list1.add( toResponse( appointment ) );
        }

        return list1;
    }

    @Override
    public List<AppointmentDtos.AppointmentSummaryResponse> toSummaryList(List<Appointment> list) {
        if ( list == null ) {
            return null;
        }

        List<AppointmentDtos.AppointmentSummaryResponse> list1 = new ArrayList<AppointmentDtos.AppointmentSummaryResponse>( list.size() );
        for ( Appointment appointment : list ) {
            list1.add( toSummary( appointment ) );
        }

        return list1;
    }

    private String aOfficeNumber(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Office office = appointment.getOffice();
        if ( office == null ) {
            return null;
        }
        String number = office.getNumber();
        if ( number == null ) {
            return null;
        }
        return number;
    }
}
