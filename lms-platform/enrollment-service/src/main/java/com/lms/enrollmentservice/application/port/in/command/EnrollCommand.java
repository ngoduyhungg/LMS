package com.lms.enrollmentservice.application.port.in.command;

public record EnrollCommand(
        String userId,
        Long courseId
) {}
