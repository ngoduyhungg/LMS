package com.lms.enrollmentservice.application.port.out;

import com.lms.enrollmentservice.application.port.out.dto.CertificateDocumentModel;

public interface PdfGeneratorPort {
    byte[] generateCertificate(CertificateDocumentModel model);
}