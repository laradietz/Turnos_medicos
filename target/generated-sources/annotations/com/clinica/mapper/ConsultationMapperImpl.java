package com.clinica.mapper;

import com.clinica.dto.ConsultationDtos;
import com.clinica.entity.Appointment;
import com.clinica.entity.Consultation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-24T23:11:31-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class ConsultationMapperImpl implements ConsultationMapper {

    @Override
    public ConsultationDtos.ConsultationResponse toResponse(Consultation c) {
        if ( c == null ) {
            return null;
        }

        UUID appointmentId = null;
        UUID id = null;
        String reason = null;
        String symptoms = null;
        String diagnosis = null;
        String treatment = null;
        String observations = null;
        BigDecimal weightKg = null;
        BigDecimal heightCm = null;
        String bloodPressure = null;
        BigDecimal temperature = null;
        Integer heartRate = null;
        LocalDateTime createdAt = null;

        appointmentId = cAppointmentId( c );
        id = c.getId();
        reason = c.getReason();
        symptoms = c.getSymptoms();
        diagnosis = c.getDiagnosis();
        treatment = c.getTreatment();
        observations = c.getObservations();
        weightKg = c.getWeightKg();
        heightCm = c.getHeightCm();
        bloodPressure = c.getBloodPressure();
        temperature = c.getTemperature();
        heartRate = c.getHeartRate();
        createdAt = c.getCreatedAt();

        ConsultationDtos.ConsultationResponse consultationResponse = new ConsultationDtos.ConsultationResponse( id, reason, symptoms, diagnosis, treatment, observations, weightKg, heightCm, bloodPressure, temperature, heartRate, appointmentId, createdAt );

        return consultationResponse;
    }

    @Override
    public List<ConsultationDtos.ConsultationResponse> toResponseList(List<Consultation> list) {
        if ( list == null ) {
            return null;
        }

        List<ConsultationDtos.ConsultationResponse> list1 = new ArrayList<ConsultationDtos.ConsultationResponse>( list.size() );
        for ( Consultation consultation : list ) {
            list1.add( toResponse( consultation ) );
        }

        return list1;
    }

    private UUID cAppointmentId(Consultation consultation) {
        if ( consultation == null ) {
            return null;
        }
        Appointment appointment = consultation.getAppointment();
        if ( appointment == null ) {
            return null;
        }
        UUID id = appointment.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
