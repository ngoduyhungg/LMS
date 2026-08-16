package com.lms.courseservice.application.port.out.dto;

public record CourseProjectionPayload(
        Long courseId,
        String instructorId,
        long totalLessons
) {}