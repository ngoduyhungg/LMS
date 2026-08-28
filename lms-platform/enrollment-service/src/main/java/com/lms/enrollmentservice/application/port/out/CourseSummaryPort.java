package com.lms.enrollmentservice.application.port.out;

import com.lms.enrollmentservice.application.port.out.dto.CourseSummary;

import java.util.List;

public interface CourseSummaryPort {
    CourseSummary getCourseSummary(Long courseId);
    List<CourseSummary> getCourseSummaries(List<Long> courseIds);
}