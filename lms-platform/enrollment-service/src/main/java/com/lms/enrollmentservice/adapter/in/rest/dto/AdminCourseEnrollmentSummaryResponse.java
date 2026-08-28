package com.lms.enrollmentservice.adapter.in.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminCourseEnrollmentSummaryResponse {
    private Long courseId;
    private String courseTitle;
    private String courseStatus;
    private Long enrollmentCount;
    private Long activeCount;
    private Long completedCount;
}