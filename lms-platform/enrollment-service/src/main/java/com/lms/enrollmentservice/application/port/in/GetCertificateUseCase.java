package com.lms.enrollmentservice.application.port.in;

import com.lms.enrollmentservice.domain.model.UserCertificate;
import java.util.List;

public interface GetCertificateUseCase {
    List<UserCertificate> getMyCertificates(String userId);
}