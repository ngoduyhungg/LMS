package com.lms.enrollmentservice.adapter.out.persistence;

import com.lms.enrollmentservice.adapter.out.persistence.entity.CourseReferenceJpaEntity;
import com.lms.enrollmentservice.adapter.out.persistence.mapper.CourseReferencePersistenceMapper;
import com.lms.enrollmentservice.adapter.out.persistence.repository.CourseReferenceJpaRepository;
import com.lms.enrollmentservice.application.port.out.CourseReferenceRepositoryPort;
import com.lms.enrollmentservice.domain.model.CourseReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CourseReferencePersistenceAdapter implements CourseReferenceRepositoryPort {

    private final CourseReferenceJpaRepository repository;
    private final CourseReferencePersistenceMapper mapper;

    @Override
    public CourseReference save(CourseReference courseReference) {
        CourseReferenceJpaEntity entity = mapper.toEntity(courseReference);
        CourseReferenceJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CourseReference> findByCourseId(Long courseId) {
        return repository.findByCourseId(courseId).map(mapper::toDomain);
    }
}