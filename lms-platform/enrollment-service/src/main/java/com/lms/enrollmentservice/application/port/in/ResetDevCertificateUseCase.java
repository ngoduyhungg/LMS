package com.lms.enrollmentservice.application.port.in;

public interface ResetDevCertificateUseCase {
    void resetCertificateByEnrollmentId(Long enrollmentId);
}