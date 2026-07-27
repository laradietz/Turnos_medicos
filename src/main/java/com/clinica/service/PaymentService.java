package com.clinica.service;

import com.clinica.dto.PaymentDtos;
import com.clinica.entity.*;
import com.clinica.exception.*;
import com.clinica.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// ── PAYMENT SERVICE ──────────────────────────────────────────
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository     paymentRepository;
    private final AppointmentRepository appointmentRepository;

    public Payment findByAppointment(UUID appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for appointment", appointmentId));
    }

    public List<Payment> findByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    @Transactional
    public Payment create(PaymentDtos.CreatePaymentRequest req) {
        Appointment appointment = appointmentRepository.findById(req.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", req.appointmentId()));

        paymentRepository.findByAppointmentId(req.appointmentId()).ifPresent(p -> {
            throw new BusinessException("Payment already exists for this appointment");
        });

        Payment payment = Payment.builder()
                .appointment(appointment)
                .totalAmount(req.totalAmount())
                .insuranceAmount(req.insuranceAmount())
                .patientAmount(req.patientAmount())
                .paymentMethod(req.paymentMethod())
                .status(Payment.PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment confirmPayment(UUID id, String transactionReference) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new BusinessException("Only PENDING payments can be confirmed");
        }

        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionReference(transactionReference);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment refund(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        if (payment.getStatus() != Payment.PaymentStatus.PAID) {
            throw new BusinessException("Only PAID payments can be refunded");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        return paymentRepository.save(payment);
    }
}
