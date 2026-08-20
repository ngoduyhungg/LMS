package com.lms.enrollmentservice.domain.model;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCertificate {
    private Long id;
    private Long certificateId;
    private String userId;
    private Long enrollmentId;
    private String certificateCode;
    private String pdfUrl;
    private ZonedDateTime issuedAt;

    // Factory method cấp mới chứng chỉ
    public static UserCertificate issueNew(Long certificateId, String userId, Long enrollmentId, String pdfUrl, String certificateCode, ZonedDateTime issuedAt) {
        return UserCertificate.builder()
                .certificateId(certificateId)
                .userId(userId)
                .enrollmentId(enrollmentId)
                .certificateCode(certificateCode)
                .pdfUrl(pdfUrl)
                .issuedAt(issuedAt)
                .build();
    }
    public static String generateCode() {
        return "CERT-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
