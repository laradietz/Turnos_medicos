package com.clinica.mapper;

import com.clinica.dto.*;
import com.clinica.entity.*;
import org.mapstruct.*;

import java.util.List;

// ── MEDICAL STUDY MAPPER ─────────────────────────────────────
@Mapper(componentModel = "spring")
public interface MedicalStudyMapper {

    @Mapping(target = "status", expression = "java(s.getStatus().name())")
    MedicalStudyDtos.MedicalStudyResponse toResponse(MedicalStudy s);

    List<MedicalStudyDtos.MedicalStudyResponse> toResponseList(List<MedicalStudy> list);
}
