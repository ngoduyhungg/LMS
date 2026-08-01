// package: com.lms.courseservice.adapter.out.persistence
package com.lms.courseservice.adapter.out.persistence;

import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.courseservice.domain.enums.CourseStatus;
import com.lms.courseservice.domain.model.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence Adapter — tầng duy nhất được phép inject CourseJpaRepository.
 *
 * Tuân thủ Hexagonal Architecture (Ports & Adapters):
 *   - Implement CourseRepositoryPort (port outbound được định nghĩa ở tầng Application).
 *   - Tầng Application Service chỉ biết đến CourseRepositoryPort, không biết đến lớp này.
 *   - Spring Boot tự động wire CoursePersistenceAdapter vào chỗ cần CourseRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class CoursePersistenceAdapter implements CourseRepositoryPort {

    private final CourseJpaRepository courseJpaRepository;

    @Override
    public Course save(Course course) {
        return courseJpaRepository.save(course);
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseJpaRepository.findById(id);
    }

    @Override
    public Optional<Course> findBySlug(String slug) {
        return courseJpaRepository.findBySlug(slug);
    }

    @Override
    public List<Course> findAllByStatus(CourseStatus status) {
        return courseJpaRepository.findAllByStatus(status);
    }

    @Override
    public List<Course> findAll() {
        return courseJpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        courseJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return courseJpaRepository.existsById(id);
    }
}
