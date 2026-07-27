package com.lms.courseservice.adapter.out.persistence;

import com.lms.courseservice.domain.model.LessonResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonResourceJpaRepository extends JpaRepository<LessonResource, Long> {
    List<LessonResource> findAllByLessonId(Long lessonId);
}