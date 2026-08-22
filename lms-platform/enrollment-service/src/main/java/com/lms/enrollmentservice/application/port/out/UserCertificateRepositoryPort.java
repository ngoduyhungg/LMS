package com.lms.enrollmentservice.application.port.out;

import com.lms.enrollmentservice.domain.model.UserCertificate;

import java.util.List;
import java.util.Optional;

public interface UserCertificateRepositoryPort {
    UserCertificate save(UserCertificate userCertificate);
    boolean existsByEnrollmentId(Long enrollmentId);
    Optional<UserCertificate> findByEnrollmentId(Long enrollmentId);
    List<UserCertificate> findByUserId(String userId);
    void deleteByEnrollmentId(Long enrollmentId);
    Optional<UserCertificate> findByCertificateCode(String certificateCode);
}
