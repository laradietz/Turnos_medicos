package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoices",
       uniqueConstraints = @UniqueConstraint(name = "uk_invoice_number",
                                              columnNames = "invoice_number"))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Invoice extends BaseEntity {

    public enum InvoiceType { A, B, C }

    @NotNull
    @Column(name = "invoice_number", nullable = false, length = 30)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false, length = 5)
    private InvoiceType invoiceType;

    @NotNull
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @NotNull
    @Column(name = "net_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "cae", length = 20)
    private String cae;

    @Column(name = "cae_expiration")
    private LocalDate caeExpiration;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;
}
