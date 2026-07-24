package com.lms.modules.course.repository;

import com.lms.modules.course.entity.Course;
import com.lms.modules.course.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findBySlug(String slug);
    List<Course> findAllByStatus(CourseStatus status);
}
