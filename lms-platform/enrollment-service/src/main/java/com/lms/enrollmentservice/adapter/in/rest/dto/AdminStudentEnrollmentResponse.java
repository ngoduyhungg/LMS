package com.lms.enrollmentservice.adapter.in.rest.dto;

import lombok.Builder;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
@Builder
public class AdminStudentEnrollmentResponse {
    private Long enrollmentId;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String status;
    private Double progressPercentage;
    private Long lastAccessedLessonId;
    private ZonedDateTime enrolledAt;
    private ZonedDateTime completedAt;
}