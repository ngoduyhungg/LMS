package com.lms.enrollmentservice.application.port.out;

import com.lms.enrollmentservice.domain.model.CourseReference;

import java.util.Optional;

public interface CourseReferenceRepositoryPort {
    CourseReference save(CourseReference courseReference);
    Optional<CourseReference> findByCourseId(Long courseId);
}