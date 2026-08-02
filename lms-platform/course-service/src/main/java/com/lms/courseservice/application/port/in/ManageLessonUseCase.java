package com.lms.courseservice.application.port.in;

import com.lms.courseservice.application.port.in.command.LessonCommand;
import com.lms.courseservice.domain.model.Lesson;

public interface ManageLessonUseCase {
    Lesson addLesson(Long moduleId, LessonCommand request);
    Lesson updateLesson(Long lessonId, LessonCommand request);
    void deleteLesson(Long lessonId);
}