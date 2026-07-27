package com.clinica.mapper;

import com.clinica.dto.PaymentDtos;
import com.clinica.entity.Payment;
import java.math.BigDecimal;
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
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentDtos.PaymentResponse toResponse(Payment p) {
        if ( p == null ) {
            return null;
        }

        UUID id = null;
        BigDecimal totalAmount = null;
        BigDecimal insuranceAmount = null;
        BigDecimal patientAmount = null;
        LocalDateTime paidAt = null;
        String transactionReference = null;

        id = p.getId();
        totalAmount = p.getTotalAmount();
        insuranceAmount = p.getInsuranceAmount();
        patientAmount = p.getPatientAmount();
        paidAt = p.getPaidAt();
        transactionReference = p.getTransactionReference();

        String status = p.getStatus().name();
        String paymentMethod = p.getPaymentMethod() != null ? p.getPaymentMethod().name() : null;

        PaymentDtos.PaymentResponse paymentResponse = new PaymentDtos.PaymentResponse( id, totalAmount, insuranceAmount, patientAmount, status, paymentMethod, paidAt, transactionReference );

        return paymentResponse;
    }

    @Override
    public List<PaymentDtos.PaymentResponse> toResponseList(List<Payment> list) {
        if ( list == null ) {
            return null;
        }

        List<PaymentDtos.PaymentResponse> list1 = new ArrayList<PaymentDtos.PaymentResponse>( list.size() );
        for ( Payment payment : list ) {
            list1.add( toResponse( payment ) );
        }

        return list1;
    }
}
