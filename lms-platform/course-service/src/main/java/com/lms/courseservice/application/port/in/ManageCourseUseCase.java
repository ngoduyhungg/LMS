package com.lms.courseservice.application.port.in;

import com.lms.courseservice.adapter.in.rest.dto.CourseResponse;
import com.lms.courseservice.adapter.in.rest.dto.CourseUpsertRequest;

public interface ManageCourseUseCase {
    CourseResponse createCourse(CourseUpsertRequest request);
    CourseResponse updateCourse(Long id, CourseUpsertRequest request);
    void deleteCourse(Long id);
}