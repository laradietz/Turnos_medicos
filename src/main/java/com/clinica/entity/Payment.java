package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Payment extends BaseEntity {

    public enum PaymentStatus  { PENDING, PAID, PARTIALLY_PAID, REFUNDED, CANCELLED }
    public enum PaymentMethod  { CASH, CREDIT_CARD, DEBIT_CARD, TRANSFER, INSURANCE }

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "insurance_amount", precision = 10, scale = 2)
    private BigDecimal insuranceAmount;

    @Column(name = "patient_amount", precision = 10, scale = 2)
    private BigDecimal patientAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(length = 255)
    private String notes;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY,
              cascade = CascadeType.ALL, orphanRemoval = true)
    private Invoice invoice;
}
