package com.clinica.mapper;

import com.clinica.dto.*;
import com.clinica.entity.*;
import org.mapstruct.*;

import java.util.List;

// ── PRESCRIPTION MAPPER ──────────────────────────────────────
@Mapper(componentModel = "spring")
public interface PrescriptionMapper {

    PrescriptionDtos.PrescriptionItemResponse toItemResponse(PrescriptionItem item);

    @Mapping(target = "items", source = "items")
    PrescriptionDtos.PrescriptionResponse toResponse(Prescription p);

    List<PrescriptionDtos.PrescriptionResponse> toResponseList(List<Prescription> list);
}
