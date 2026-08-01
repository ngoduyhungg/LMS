package com.lms.courseservice.application.port.out;

import com.lms.courseservice.domain.model.Lesson;
import java.util.List;
import java.util.Optional;

public interface LessonRepositoryPort {
    Lesson save(Lesson lesson);
    Optional<Lesson> findById(Long id);
    List<Lesson> findAllByModuleIdOrderBySortOrder(Long moduleId);
    void deleteById(Long id);
}