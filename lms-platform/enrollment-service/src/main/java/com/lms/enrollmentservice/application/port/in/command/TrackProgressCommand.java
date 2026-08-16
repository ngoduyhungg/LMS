package com.lms.enrollmentservice.application.port.in.command;

public record TrackProgressCommand(
        String userId,
        Long courseId,
        Long lessonId,
        int watchedSeconds,
        boolean isCompleted
) {}
