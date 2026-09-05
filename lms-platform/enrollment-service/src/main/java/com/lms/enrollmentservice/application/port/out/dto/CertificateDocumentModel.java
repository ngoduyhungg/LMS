package com.lms.enrollmentservice.application.port.out.dto;

import java.time.LocalDate;

public record CertificateDocumentModel(
        String studentName,
        String courseTitle,
        String certificateCode,
        LocalDate issueDate,
        String templateUrl,
        String templateTitle
) {}