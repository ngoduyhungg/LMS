package com.lms.enrollmentservice.adapter.out.persistence.repository;

import com.lms.enrollmentservice.adapter.out.persistence.entity.CourseReferenceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseReferenceJpaRepository extends JpaRepository<CourseReferenceJpaEntity, Long> {
    Optional<CourseReferenceJpaEntity> findByCourseId(Long courseId);
}