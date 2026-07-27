package com.clinica.mapper;

import com.clinica.dto.PatientDtos;
import com.clinica.entity.Patient;
import com.clinica.entity.PatientInsurance;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "gender",    expression = "java(patient.getGender()    != null ? patient.getGender().name()    : null)")
    @Mapping(target = "bloodType", expression = "java(patient.getBloodType() != null ? patient.getBloodType().name() : null)")
    PatientDtos.PatientResponse toResponse(Patient patient);

    List<PatientDtos.PatientResponse> toResponseList(List<Patient> patients);

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "active",      ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    @Mapping(target = "deletedAt",   ignore = true)
    @Mapping(target = "insurances",  ignore = true)
    @Mapping(target = "appointments",ignore = true)
    @Mapping(target = "clinicalRecord", ignore = true)
    Patient toEntity(PatientDtos.CreatePatientRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "dni",          ignore = true)
    @Mapping(target = "birthDate",    ignore = true)
    @Mapping(target = "active",       ignore = true)
    @Mapping(target = "createdAt",    ignore = true)
    @Mapping(target = "updatedAt",    ignore = true)
    @Mapping(target = "deletedAt",    ignore = true)
    @Mapping(target = "insurances",   ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "clinicalRecord", ignore = true)
    void updateFromRequest(PatientDtos.UpdatePatientRequest request,
                           @MappingTarget Patient patient);

    @Mapping(target = "healthInsuranceName",
             source = "healthInsurance.name")
    PatientDtos.PatientInsuranceResponse toInsuranceResponse(PatientInsurance pi);

    List<PatientDtos.PatientInsuranceResponse> toInsuranceResponseList(List<PatientInsurance> list);
}
