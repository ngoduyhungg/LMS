package com.lms.enrollmentservice.adapter.out.persistence;

import com.lms.enrollmentservice.adapter.out.persistence.entity.UserCertificateJpaEntity;
import com.lms.enrollmentservice.adapter.out.persistence.mapper.UserCertificatePersistenceMapper;
import com.lms.enrollmentservice.adapter.out.persistence.repository.UserCertificateJpaRepository;
import com.lms.enrollmentservice.application.port.out.UserCertificateRepositoryPort;
import com.lms.enrollmentservice.domain.model.UserCertificate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserCertificatePersistenceAdapter implements UserCertificateRepositoryPort {

    private final UserCertificateJpaRepository repository;
    private final UserCertificatePersistenceMapper mapper;

    @Override
    public UserCertificate save(UserCertificate userCertificate) {
        UserCertificateJpaEntity entity = mapper.toEntity(userCertificate);
        UserCertificateJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByEnrollmentId(Long enrollmentId) {
        return repository.existsByEnrollmentId(enrollmentId);
    }

    @Override
    public Optional<UserCertificate> findByEnrollmentId(Long enrollmentId) {
        return repository.findByEnrollmentId(enrollmentId).map(mapper::toDomain);
    }
    @Override
    public List<UserCertificate> findByUserId(String userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }
    @Override
    public void deleteByEnrollmentId(Long enrollmentId) {
        repository.deleteByEnrollmentId(enrollmentId);
    }
    @Override
    public Optional<UserCertificate> findByCertificateCode(String certificateCode) {
        return repository.findByCertificateCode(certificateCode)
                .map(mapper::toDomain);
    }
    @Override
    public boolean existsByCertificateId(Long certificateId) {
        return repository.existsByCertificateId(certificateId);
    }
}