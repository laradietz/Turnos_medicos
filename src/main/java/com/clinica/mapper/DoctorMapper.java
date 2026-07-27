package com.clinica.mapper;

import com.clinica.dto.DoctorDtos;
import com.clinica.entity.Doctor;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SpecialtyMapper.class})
public interface DoctorMapper {

    @Mapping(target = "specialty", source = "specialty")
    DoctorDtos.DoctorResponse toResponse(Doctor doctor);

    @Mapping(target = "fullName",
             expression = "java(doctor.getFirstName() + ' ' + doctor.getLastName())")
    @Mapping(target = "specialtyName", source = "specialty.name")
    DoctorDtos.DoctorSummaryResponse toSummary(Doctor doctor);

    List<DoctorDtos.DoctorResponse>        toResponseList(List<Doctor> doctors);
    List<DoctorDtos.DoctorSummaryResponse> toSummaryList(List<Doctor> doctors);

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "active",      ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    @Mapping(target = "deletedAt",   ignore = true)
    @Mapping(target = "specialty",   ignore = true)
    @Mapping(target = "user",        ignore = true)
    @Mapping(target = "offices",     ignore = true)
    @Mapping(target = "schedules",   ignore = true)
    @Mapping(target = "appointments",ignore = true)
    Doctor toEntity(DoctorDtos.CreateDoctorRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "licenseNumber", ignore = true)
    @Mapping(target = "active",        ignore = true)
    @Mapping(target = "createdAt",     ignore = true)
    @Mapping(target = "updatedAt",     ignore = true)
    @Mapping(target = "deletedAt",     ignore = true)
    @Mapping(target = "specialty",     ignore = true)
    @Mapping(target = "user",          ignore = true)
    @Mapping(target = "offices",       ignore = true)
    @Mapping(target = "schedules",     ignore = true)
    @Mapping(target = "appointments",  ignore = true)
    void updateFromRequest(DoctorDtos.UpdateDoctorRequest request,
                           @MappingTarget Doctor doctor);
}
