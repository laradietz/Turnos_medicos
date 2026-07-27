package com.clinica.mapper;

import com.clinica.dto.MedicalStudyDtos;
import com.clinica.entity.MedicalStudy;
import java.time.LocalDate;
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
public class MedicalStudyMapperImpl implements MedicalStudyMapper {

    @Override
    public MedicalStudyDtos.MedicalStudyResponse toResponse(MedicalStudy s) {
        if ( s == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String studyType = null;
        LocalDate orderedDate = null;
        LocalDate resultDate = null;
        String result = null;
        String fileUrl = null;
        String notes = null;

        id = s.getId();
        name = s.getName();
        studyType = s.getStudyType();
        orderedDate = s.getOrderedDate();
        resultDate = s.getResultDate();
        result = s.getResult();
        fileUrl = s.getFileUrl();
        notes = s.getNotes();

        String status = s.getStatus().name();

        MedicalStudyDtos.MedicalStudyResponse medicalStudyResponse = new MedicalStudyDtos.MedicalStudyResponse( id, name, studyType, orderedDate, resultDate, status, result, fileUrl, notes );

        return medicalStudyResponse;
    }

    @Override
    public List<MedicalStudyDtos.MedicalStudyResponse> toResponseList(List<MedicalStudy> list) {
        if ( list == null ) {
            return null;
        }

        List<MedicalStudyDtos.MedicalStudyResponse> list1 = new ArrayList<MedicalStudyDtos.MedicalStudyResponse>( list.size() );
        for ( MedicalStudy medicalStudy : list ) {
            list1.add( toResponse( medicalStudy ) );
        }

        return list1;
    }
}
