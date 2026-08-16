package com.lms.enrollmentservice.application.port.out;

public interface CourseValidationPort {
    boolean isLessonValidForCourse(Long courseId, Long lessonId);
}