package com.lms.enrollmentservice.application.port.in;

import com.lms.enrollmentservice.domain.model.UserCertificate;

public interface ManageCertificateUseCase {
    UserCertificate issueCertificateForEnrollment(Long enrollmentId);
}
