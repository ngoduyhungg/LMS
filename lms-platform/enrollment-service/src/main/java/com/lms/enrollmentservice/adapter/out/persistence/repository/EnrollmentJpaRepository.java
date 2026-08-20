package com.lms.enrollmentservice.adapter.out.persistence.repository;

import com.lms.enrollmentservice.adapter.out.persistence.entity.EnrollmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentJpaEntity, Long> {
    Optional<EnrollmentJpaEntity> findByUserIdAndCourseId(String userId, Long courseId);
    boolean existsByUserIdAndCourseId(String userId, Long courseId);
    List<EnrollmentJpaEntity> findByUserId(String userId);
}
