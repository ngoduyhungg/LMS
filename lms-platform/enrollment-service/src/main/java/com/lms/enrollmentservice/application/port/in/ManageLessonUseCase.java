package com.lms.courseservice.application.port.in;

import com.lms.courseservice.adapter.in.rest.dto.LessonResponse;
import com.lms.courseservice.adapter.in.rest.dto.LessonUpsertRequest;

public interface ManageLessonUseCase {
    LessonResponse addLesson(Long moduleId, LessonUpsertRequest request);
    LessonResponse updateLesson(Long lessonId, LessonUpsertRequest request);
    void deleteLesson(Long lessonId);
}