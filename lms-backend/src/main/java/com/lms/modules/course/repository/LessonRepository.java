package com.lms.modules.course.repository;

import com.lms.modules.course.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllByModuleIdOrderBySortOrder(Long moduleId);
}
