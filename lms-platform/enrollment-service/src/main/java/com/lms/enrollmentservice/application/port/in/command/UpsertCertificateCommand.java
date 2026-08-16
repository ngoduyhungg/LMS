package com.lms.enrollmentservice.application.port.in.command;

public record UpsertCertificateCommand(
        Long courseId,
        String title,
        String templateUrl,
        String currentUserId
) {}