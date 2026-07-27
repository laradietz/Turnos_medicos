package com.clinica.mapper;

import com.clinica.dto.PrescriptionDtos;
import com.clinica.entity.Prescription;
import com.clinica.entity.PrescriptionItem;
import java.time.LocalDate;
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
public class PrescriptionMapperImpl implements PrescriptionMapper {

    @Override
    public PrescriptionDtos.PrescriptionItemResponse toItemResponse(PrescriptionItem item) {
        if ( item == null ) {
            return null;
        }

        UUID id = null;
        String medicationName = null;
        String dosage = null;
        String frequency = null;
        String duration = null;
        String instructions = null;
        Integer quantity = null;

        id = item.getId();
        medicationName = item.getMedicationName();
        dosage = item.getDosage();
        frequency = item.getFrequency();
        duration = item.getDuration();
        instructions = item.getInstructions();
        quantity = item.getQuantity();

        PrescriptionDtos.PrescriptionItemResponse prescriptionItemResponse = new PrescriptionDtos.PrescriptionItemResponse( id, medicationName, dosage, frequency, duration, instructions, quantity );

        return prescriptionItemResponse;
    }

    @Override
    public PrescriptionDtos.PrescriptionResponse toResponse(Prescription p) {
        if ( p == null ) {
            return null;
        }

        List<PrescriptionDtos.PrescriptionItemResponse> items = null;
        UUID id = null;
        LocalDate issueDate = null;
        LocalDate expirationDate = null;
        String prescriptionNumber = null;
        String notes = null;
        LocalDateTime createdAt = null;

        items = prescriptionItemListToPrescriptionItemResponseList( p.getItems() );
        id = p.getId();
        issueDate = p.getIssueDate();
        expirationDate = p.getExpirationDate();
        prescriptionNumber = p.getPrescriptionNumber();
        notes = p.getNotes();
        createdAt = p.getCreatedAt();

        PrescriptionDtos.PrescriptionResponse prescriptionResponse = new PrescriptionDtos.PrescriptionResponse( id, issueDate, expirationDate, prescriptionNumber, notes, items, createdAt );

        return prescriptionResponse;
    }

    @Override
    public List<PrescriptionDtos.PrescriptionResponse> toResponseList(List<Prescription> list) {
        if ( list == null ) {
            return null;
        }

        List<PrescriptionDtos.PrescriptionResponse> list1 = new ArrayList<PrescriptionDtos.PrescriptionResponse>( list.size() );
        for ( Prescription prescription : list ) {
            list1.add( toResponse( prescription ) );
        }

        return list1;
    }

    protected List<PrescriptionDtos.PrescriptionItemResponse> prescriptionItemListToPrescriptionItemResponseList(List<PrescriptionItem> list) {
        if ( list == null ) {
            return null;
        }

        List<PrescriptionDtos.PrescriptionItemResponse> list1 = new ArrayList<PrescriptionDtos.PrescriptionItemResponse>( list.size() );
        for ( PrescriptionItem prescriptionItem : list ) {
            list1.add( toItemResponse( prescriptionItem ) );
        }

        return list1;
    }
}
