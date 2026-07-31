package com.lms.modules.auth.entity;

import com.lms.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Table(name = "refresh_tokens")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "token", length = 500, nullable = false, unique = true)
    private String token;
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
    @Column(name = "revoked", nullable = false)
    @Builder.Default
    private Boolean revoked = false;
}
