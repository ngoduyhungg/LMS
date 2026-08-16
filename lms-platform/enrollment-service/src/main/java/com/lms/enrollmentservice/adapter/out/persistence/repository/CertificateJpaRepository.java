package com.lms.enrollmentservice.adapter.out.persistence.repository;

import com.lms.enrollmentservice.adapter.out.persistence.entity.CertificateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificateJpaRepository extends JpaRepository<CertificateJpaEntity, Long> {
    Optional<CertificateJpaEntity> findByCourseId(Long courseId);
}