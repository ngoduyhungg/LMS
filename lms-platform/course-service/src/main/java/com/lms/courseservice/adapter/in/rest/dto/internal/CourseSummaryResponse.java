package com.lms.courseservice.adapter.in.rest.dto.internal;

import com.lms.courseservice.domain.enums.CourseStatus;

public record CourseSummaryResponse(Long courseId, String title, CourseStatus status) {}
