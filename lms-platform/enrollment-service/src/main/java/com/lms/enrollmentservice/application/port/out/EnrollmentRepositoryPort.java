package com.lms.enrollmentservice.application.port.out;

import com.lms.enrollmentservice.application.port.out.dto.CourseEnrollmentAggregation;
import com.lms.enrollmentservice.domain.model.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepositoryPort {
    Enrollment save(Enrollment enrollment);
    Optional<Enrollment> findById(Long id);
    Optional<Enrollment> findByUserIdAndCourseId(String userId, Long courseId);
    boolean existsByUserIdAndCourseId(String userId, Long courseId);
    List<Enrollment> findByUserId(String userId);
    List<Enrollment> findAll();
    List<CourseEnrollmentAggregation> getCourseEnrollmentAggregations();
    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);
}
