package com.lms.enrollmentservice.application.port.out.dto;

public record CourseEnrollmentAggregation(
        Long courseId,
        Long enrollmentCount,
        Long activeCount,
        Long completedCount
) {}