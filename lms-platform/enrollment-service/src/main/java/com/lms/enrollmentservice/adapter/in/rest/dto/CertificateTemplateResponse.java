package com.lms.enrollmentservice.adapter.in.rest.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CertificateTemplateResponse {
    private Long id;
    private Long courseId;
    private String title;
    private String templateUrl;
}