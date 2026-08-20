package com.lms.enrollmentservice.adapter.in.rest.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.ZonedDateTime;

@Getter
@Builder
public class UserCertificateResponse {
    private String certificateCode;
    private Long enrollmentId;
    private String pdfUrl;
    private ZonedDateTime issuedAt;
}