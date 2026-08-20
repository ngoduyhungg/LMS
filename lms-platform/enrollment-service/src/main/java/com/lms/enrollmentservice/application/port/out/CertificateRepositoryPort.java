package com.lms.enrollmentservice.application.port.out;

import com.lms.enrollmentservice.domain.model.Certificate;
import java.util.Optional;

public interface CertificateRepositoryPort {
    Certificate save(Certificate certificate);
    Optional<Certificate> findByCourseId(Long courseId);
}
