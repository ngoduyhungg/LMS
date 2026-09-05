package com.lms.enrollmentservice.adapter.out.persistence.repository;

import com.lms.enrollmentservice.adapter.out.persistence.entity.CertificateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CertificateJpaRepository extends JpaRepository<CertificateJpaEntity, Long> {
    Optional<CertificateJpaEntity> findByCourseId(Long courseId);
}