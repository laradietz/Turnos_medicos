package com.clinica.dto;

import com.clinica.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

public class NotificationDtos {

    public record NotificationResponse(
        UUID id,
        String type,
        String channel,
        String subject,
        Boolean isRead,
        LocalDateTime sentAt,
        LocalDateTime createdAt
    ) {}
}
