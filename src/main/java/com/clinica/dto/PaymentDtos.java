package com.clinica.dto;

import com.clinica.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

public class PaymentDtos {

    public record CreatePaymentRequest(
        @NotNull UUID appointmentId,
        @NotNull @DecimalMin("0") BigDecimal totalAmount,
        BigDecimal insuranceAmount,
        BigDecimal patientAmount,
        @NotNull Payment.PaymentMethod paymentMethod
    ) {}

    public record PaymentResponse(
        UUID id,
        BigDecimal totalAmount,
        BigDecimal insuranceAmount,
        BigDecimal patientAmount,
        String status,
        String paymentMethod,
        LocalDateTime paidAt,
        String transactionReference
    ) {}
}
