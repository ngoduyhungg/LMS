package com.lms.enrollmentservice.adapter.out.persistence.repository;

import com.lms.enrollmentservice.adapter.out.persistence.entity.UserCertificateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCertificateJpaRepository extends JpaRepository<UserCertificateJpaEntity, Long> {
    Optional<UserCertificateJpaEntity> findByEnrollmentId(Long enrollmentId);
    boolean existsByEnrollmentId(Long enrollmentId);
    List<UserCertificateJpaEntity> findByUserId(String userId);
    void deleteByEnrollmentId(Long enrollmentId);
}