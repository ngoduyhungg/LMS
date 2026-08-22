package com.lms.enrollmentservice.application.port.in;

public interface DownloadCertificateUseCase {
    byte[] downloadCertificatePdf(String certificateCode);
}