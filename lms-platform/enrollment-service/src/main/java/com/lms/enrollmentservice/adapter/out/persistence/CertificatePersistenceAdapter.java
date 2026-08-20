package com.lms.enrollmentservice.adapter.out.persistence;

import com.lms.enrollmentservice.adapter.out.persistence.mapper.CertificatePersistenceMapper;
import com.lms.enrollmentservice.adapter.out.persistence.repository.CertificateJpaRepository;
import com.lms.enrollmentservice.application.port.out.CertificateRepositoryPort;
import com.lms.enrollmentservice.domain.model.Certificate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CertificatePersistenceAdapter implements CertificateRepositoryPort {

    private final CertificateJpaRepository repository;
    private final CertificatePersistenceMapper mapper;

    @Override
    public Optional<Certificate> findByCourseId(Long courseId) {
        return repository.findByCourseId(courseId).map(mapper::toDomain);
    }
    @Override
    public Certificate save(Certificate certificate){
        var entity = mapper.toEntity(certificate);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}