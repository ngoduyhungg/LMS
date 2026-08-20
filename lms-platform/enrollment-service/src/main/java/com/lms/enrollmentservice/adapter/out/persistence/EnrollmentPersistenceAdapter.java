package com.lms.enrollmentservice.adapter.out.persistence;

import com.lms.enrollmentservice.adapter.out.persistence.entity.EnrollmentJpaEntity;
import com.lms.enrollmentservice.adapter.out.persistence.mapper.EnrollmentPersistenceMapper;
import com.lms.enrollmentservice.adapter.out.persistence.repository.EnrollmentJpaRepository;
import com.lms.enrollmentservice.application.port.out.EnrollmentRepositoryPort;
import com.lms.enrollmentservice.domain.model.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EnrollmentPersistenceAdapter implements EnrollmentRepositoryPort {

    private final EnrollmentJpaRepository enrollmentJpaRepository;
    private final EnrollmentPersistenceMapper enrollmentMapper;

    @Override
    public Enrollment save(Enrollment enrollment) {
        EnrollmentJpaEntity entity = enrollmentMapper.toEntity(enrollment);
        EnrollmentJpaEntity saved = enrollmentJpaRepository.save(entity);
        return enrollmentMapper.toDomain(saved);
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        return enrollmentJpaRepository.findById(id).map(enrollmentMapper::toDomain);
    }

    @Override
    public Optional<Enrollment> findByUserIdAndCourseId(String userId, Long courseId) {
        return enrollmentJpaRepository.findByUserIdAndCourseId(userId, courseId)
                .map(enrollmentMapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndCourseId(String userId, Long courseId) {
        return enrollmentJpaRepository.existsByUserIdAndCourseId(userId, courseId);
    }
    @Override
    public List<Enrollment> findByUserId(String userId) {
        return enrollmentJpaRepository.findByUserId(userId).stream()
                .map(enrollmentMapper::toDomain)
                .toList();
    }
    @Override
    public List<Enrollment> findAll() {
        return enrollmentJpaRepository.findAll().stream()
                .map(enrollmentMapper::toDomain)
                .toList();
    }
}