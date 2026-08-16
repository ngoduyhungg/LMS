package com.lms.enrollmentservice.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CertificateTemplateRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Template URL is required")
        String templateUrl
) {}