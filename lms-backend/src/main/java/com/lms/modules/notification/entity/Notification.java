package com.lms.modules.notification.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.auth.entity.User;
import com.lms.modules.notification.enums.NotificationsType;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "notifications")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;
    @Column(name = "title", length = 255, nullable = false)
    private String title;
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    @Column(name = "notification_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationsType notificationType = NotificationsType.GENERAL;
    @Column(name = "reference_url", length = 500)
    private String referenceUrl;
}
