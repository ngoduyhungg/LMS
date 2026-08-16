package com.lms.enrollmentservice.application.port.out;

import com.lms.enrollmentservice.application.port.out.dto.CourseSummary;

public interface CourseSummaryPort {
    CourseSummary getCourseSummary(Long courseId);
}