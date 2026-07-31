package com.lms.modules.certificate.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.auth.entity.User;
import com.lms.modules.enrollment.entity.Enrollment;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Table(name = "user_certificates", uniqueConstraints = {@UniqueConstraint(name = "uk_user_certificates_user_cert", columnNames = {"user_id", "certificate_id"})})
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCertificate extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_id", nullable = false)
    private Certificate cert;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment;
    @Column(name = "certificate_code", length = 100, nullable = false, unique = true)
    private String certificateCode;
    @Column(name = "pdf_url", length = 500, nullable = false)
    private String pdfUrl;
    @Column(name = "issued_at", nullable = false)
    @Builder.Default
    private OffsetDateTime issuedAt = OffsetDateTime.now();
}
