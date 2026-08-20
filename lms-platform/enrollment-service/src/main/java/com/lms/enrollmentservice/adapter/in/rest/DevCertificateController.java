package com.lms.enrollmentservice.adapter.in.rest;

import com.lms.enrollmentservice.application.port.in.ResetDevCertificateUseCase;
import com.lms.enrollmentservice.application.service.CertificateApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/dev/certificates")
@Profile("!prod")
@RequiredArgsConstructor
public class DevCertificateController {

    private final ResetDevCertificateUseCase resetDevCertificateUseCase;
    private final CertificateApplicationService certificateApplicationService;

    @DeleteMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<Void> resetCertificate(@PathVariable Long enrollmentId) {
        resetDevCertificateUseCase.resetCertificateByEnrollmentId(enrollmentId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
    @PostMapping("/enrollment/{enrollmentId}/trigger")
    public ResponseEntity<Void> triggerCertificateGen(@PathVariable Long enrollmentId) {
        // Gọi thẳng vào hàm cấp bằng của Application Service
        certificateApplicationService.issueCertificateForEnrollment(enrollmentId);
        return ResponseEntity.ok().build();
    }
}