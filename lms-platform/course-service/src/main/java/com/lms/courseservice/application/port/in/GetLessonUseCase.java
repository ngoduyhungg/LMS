package com.lms.courseservice.application.port.in;

import com.lms.courseservice.domain.model.Lesson;
import java.util.List;

public interface GetLessonUseCase {
    List<Lesson> getLessonsByModuleId(Long moduleId);
    Lesson getLessonById(Long lessonId);
}
