package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications",
       indexes = {
           @Index(name = "idx_notification_user",      columnList = "user_id"),
           @Index(name = "idx_notification_sent_at",   columnList = "sent_at"),
           @Index(name = "idx_notification_read",      columnList = "is_read")
       })
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Notification extends BaseEntity {

    public enum NotificationType {
        APPOINTMENT_REMINDER, APPOINTMENT_CONFIRMED, APPOINTMENT_CANCELLED,
        APPOINTMENT_RESCHEDULED, STUDY_READY, PRESCRIPTION_READY, GENERAL
    }

    public enum NotificationChannel {
        EMAIL, SMS, PUSH, IN_APP
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
}
