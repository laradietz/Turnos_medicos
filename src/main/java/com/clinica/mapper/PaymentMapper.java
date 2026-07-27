package com.clinica.mapper;

import com.clinica.dto.*;
import com.clinica.entity.*;
import org.mapstruct.*;

import java.util.List;

// ── PAYMENT MAPPER ───────────────────────────────────────────
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "status",        expression = "java(p.getStatus().name())")
    @Mapping(target = "paymentMethod",
             expression = "java(p.getPaymentMethod() != null ? p.getPaymentMethod().name() : null)")
    PaymentDtos.PaymentResponse toResponse(Payment p);

    List<PaymentDtos.PaymentResponse> toResponseList(List<Payment> list);
}
