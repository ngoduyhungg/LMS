package com.lms.modules.notification.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Table(name = "user_notifications", uniqueConstraints = {@UniqueConstraint(name = "uk_user_notifications_recipient_notif", columnNames = {"recipient_id", "notification_id"})})
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotification extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notif;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;
    @Column(name = "read_at")
    private OffsetDateTime readAt;
}
