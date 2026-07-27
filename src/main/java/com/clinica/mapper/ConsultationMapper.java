package com.clinica.mapper;

import com.clinica.dto.*;
import com.clinica.entity.*;
import org.mapstruct.*;

import java.util.List;

// ── CONSULTATION MAPPER ──────────────────────────────────────
@Mapper(componentModel = "spring")
public interface ConsultationMapper {

    @Mapping(target = "appointmentId", source = "appointment.id")
    ConsultationDtos.ConsultationResponse toResponse(Consultation c);

    List<ConsultationDtos.ConsultationResponse> toResponseList(List<Consultation> list);
}
