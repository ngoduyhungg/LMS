package com.lms.courseservice.adapter.out.persistence;


import com.lms.courseservice.domain.enums.CourseStatus;
import com.lms.courseservice.domain.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseJpaRepository extends JpaRepository<Course, Long> {
    Optional<Course> findBySlug(String slug);
    List<Course> findAllByStatus(CourseStatus status);
}
