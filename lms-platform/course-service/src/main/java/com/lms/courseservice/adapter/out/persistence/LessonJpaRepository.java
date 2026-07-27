package com.lms.courseservice.adapter.out.persistence;

import com.lms.courseservice.domain.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonJpaRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllByModuleIdOrderBySortOrder(Long moduleId);
}
