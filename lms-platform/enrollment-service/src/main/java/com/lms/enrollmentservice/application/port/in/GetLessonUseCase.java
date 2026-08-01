package com.lms.courseservice.application.port.in;

import com.lms.courseservice.adapter.in.rest.dto.LessonResponse;

import java.util.List;

public interface GetLessonUseCase {
    List<LessonResponse> getLessonsByModuleId(Long moduleId);
    LessonResponse getLessonById(Long lessonId);
}
