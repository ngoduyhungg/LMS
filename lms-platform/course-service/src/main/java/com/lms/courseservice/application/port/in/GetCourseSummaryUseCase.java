package com.lms.courseservice.application.port.in;

import com.lms.courseservice.domain.model.Course;

public interface GetCourseSummaryUseCase {
    Course getCourseSummary(Long courseId);
}