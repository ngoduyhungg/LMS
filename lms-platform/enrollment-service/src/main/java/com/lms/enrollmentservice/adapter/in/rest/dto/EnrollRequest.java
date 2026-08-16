package com.lms.enrollmentservice.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollRequest(
        @NotNull(message = "Course ID is required")
        Long courseId
) {}
