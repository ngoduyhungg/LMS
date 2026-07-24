package com.lms.modules.course.repository;

import com.lms.modules.course.entity.LessonResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonResourceRepository extends JpaRepository<LessonResource, Long> {
    List<LessonResource> findAllByLessonId(Long lessonId);
}