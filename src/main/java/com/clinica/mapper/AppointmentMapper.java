package com.clinica.mapper;

import com.clinica.dto.AppointmentDtos;
import com.clinica.entity.Appointment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PatientMapper.class, DoctorMapper.class})
public interface AppointmentMapper {

    @Mapping(target = "status",      expression = "java(a.getStatus().name())")
    @Mapping(target = "type",        expression = "java(a.getType().name())")
    @Mapping(target = "patient",     source = "patient")
    @Mapping(target = "doctor",      source = "doctor")
    @Mapping(target = "officeNumber",source = "office.number")
    AppointmentDtos.AppointmentResponse toResponse(Appointment a);

    @Mapping(target = "status",
             expression = "java(a.getStatus().name())")
    @Mapping(target = "patientFullName",
             expression = "java(a.getPatient().getFirstName() + ' ' + a.getPatient().getLastName())")
    @Mapping(target = "doctorFullName",
             expression = "java(a.getDoctor().getFirstName() + ' ' + a.getDoctor().getLastName())")
    @Mapping(target = "type",
             expression = "java(a.getType().name())")
    AppointmentDtos.AppointmentSummaryResponse toSummary(Appointment a);

    List<AppointmentDtos.AppointmentResponse>        toResponseList(List<Appointment> list);
    List<AppointmentDtos.AppointmentSummaryResponse> toSummaryList(List<Appointment> list);
}
