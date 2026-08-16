package com.lms.enrollmentservice.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "user_certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCertificateJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_id", nullable = false)
    private Long certificateId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "enrollment_id", nullable = false, unique = true)
    private Long enrollmentId;

    @Column(name = "certificate_code", nullable = false, length = 100, unique = true)
    private String certificateCode;

    @Column(name = "pdf_url", nullable = false, length = 500)
    private String pdfUrl;

    @Column(name = "issued_at", nullable = false)
    private ZonedDateTime issuedAt;
}