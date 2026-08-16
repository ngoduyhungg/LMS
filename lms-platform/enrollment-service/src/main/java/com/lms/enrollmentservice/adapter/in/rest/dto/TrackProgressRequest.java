package com.lms.enrollmentservice.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record TrackProgressRequest(
        @NotNull(message = "Lesson ID is required")
        Long lessonId,

        @Min(value = 0, message = "Watched seconds cannot be negative")
        int watchedSeconds,

        boolean isCompleted
) {}
