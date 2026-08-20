package com.lms.enrollmentservice.application.port.in;

import com.lms.enrollmentservice.application.port.in.command.UpsertCertificateCommand;
import com.lms.enrollmentservice.domain.model.Certificate;

public interface ManageCertificateTemplateUseCase {
    Certificate upsertCertificateTemplate(UpsertCertificateCommand command);
}