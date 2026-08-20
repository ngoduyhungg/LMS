package com.lms.enrollmentservice.adapter.in.rest.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
public class EnrollmentResponse {
    private Long id;
    private Long courseId;
    private String status;
    private BigDecimal progressPercentage;
    private Long lastAccessedLessonId;
    private ZonedDateTime enrolledAt;
    private ZonedDateTime completedAt;
    private List<LessonProgressResponse> lessonProgresses;
}
