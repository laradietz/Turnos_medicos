package com.clinica.mapper;

import com.clinica.dto.*;
import com.clinica.entity.*;
import org.mapstruct.*;

import java.util.List;

// ── SPECIALTY MAPPER ─────────────────────────────────────────
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyDtos.SpecialtyResponse toResponse(Specialty specialty);

    List<SpecialtyDtos.SpecialtyResponse> toResponseList(List<Specialty> specialties);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(SpecialtyDtos.CreateSpecialtyRequest request,
                           @MappingTarget Specialty specialty);
}
