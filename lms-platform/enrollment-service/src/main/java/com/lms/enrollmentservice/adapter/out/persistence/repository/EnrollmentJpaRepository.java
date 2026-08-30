package com.lms.enrollmentservice.adapter.out.persistence.repository;

import com.lms.enrollmentservice.adapter.out.persistence.entity.EnrollmentJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentJpaEntity, Long> {
    Optional<EnrollmentJpaEntity> findByUserIdAndCourseId(String userId, Long courseId);
    boolean existsByUserIdAndCourseId(String userId, Long courseId);
    List<EnrollmentJpaEntity> findByUserId(String userId);
    @Query("SELECT e.courseId AS courseId, " +
            "COUNT(e) AS enrollmentCount, " +
            "SUM(CASE WHEN e.status = com.lms.enrollmentservice.domain.enums.EnrollmentStatus.ACTIVE THEN 1 ELSE 0 END) AS activeCount, " +
            "SUM(CASE WHEN e.status = com.lms.enrollmentservice.domain.enums.EnrollmentStatus.COMPLETED THEN 1 ELSE 0 END) AS completedCount " +
            "FROM EnrollmentJpaEntity e GROUP BY e.courseId")
    List<CourseEnrollmentAggregationProjection> aggregateCourseEnrollments();
    Page<EnrollmentJpaEntity> findByCourseId(Long courseId, Pageable pageable);
}
