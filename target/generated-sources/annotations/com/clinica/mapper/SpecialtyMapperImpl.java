package com.clinica.mapper;

import com.clinica.dto.SpecialtyDtos;
import com.clinica.entity.Specialty;
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
public class SpecialtyMapperImpl implements SpecialtyMapper {

    @Override
    public SpecialtyDtos.SpecialtyResponse toResponse(Specialty specialty) {
        if ( specialty == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String description = null;
        Boolean active = null;

        id = specialty.getId();
        name = specialty.getName();
        description = specialty.getDescription();
        active = specialty.getActive();

        SpecialtyDtos.SpecialtyResponse specialtyResponse = new SpecialtyDtos.SpecialtyResponse( id, name, description, active );

        return specialtyResponse;
    }

    @Override
    public List<SpecialtyDtos.SpecialtyResponse> toResponseList(List<Specialty> specialties) {
        if ( specialties == null ) {
            return null;
        }

        List<SpecialtyDtos.SpecialtyResponse> list = new ArrayList<SpecialtyDtos.SpecialtyResponse>( specialties.size() );
        for ( Specialty specialty : specialties ) {
            list.add( toResponse( specialty ) );
        }

        return list;
    }

    @Override
    public void updateFromRequest(SpecialtyDtos.CreateSpecialtyRequest request, Specialty specialty) {
        if ( request == null ) {
            return;
        }

        if ( request.name() != null ) {
            specialty.setName( request.name() );
        }
        if ( request.description() != null ) {
            specialty.setDescription( request.description() );
        }
    }
}
