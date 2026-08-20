package com.lms.enrollmentservice.application.port.in;

public interface SyncCourseReferenceUseCase {
    void syncReference(Long courseId, String instructorId, long totalLessons);
}
