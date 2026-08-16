package com.lms.courseservice.application.port.in;

public interface ValidateLessonUseCase {
    boolean validateLessonInCourse(Long courseId, Long lessonId);
}
