package com.lms.enrollmentservice.adapter.in.rest;

import com.lms.enrollmentservice.adapter.in.rest.dto.CertificateTemplateRequest;
import com.lms.enrollmentservice.adapter.in.rest.dto.CertificateTemplateResponse;
import com.lms.enrollmentservice.adapter.in.rest.mapper.InstructorCertificateRestMapper;
import com.lms.enrollmentservice.application.port.in.ManageCertificateTemplateUseCase;
import com.lms.enrollmentservice.application.port.in.command.UpsertCertificateCommand;
import com.lms.enrollmentservice.domain.model.Certificate;
import com.lms.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instructor/courses")
@RequiredArgsConstructor
public class InstructorCertificateController {

    private final ManageCertificateTemplateUseCase manageCertificateTemplateUseCase;
    private final InstructorCertificateRestMapper restMapper;

    @PutMapping("/{courseId}/certificate-template")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CertificateTemplateResponse> upsertCertificateTemplate(
            @PathVariable Long courseId,
            @Valid @RequestBody CertificateTemplateRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        UpsertCertificateCommand command = restMapper.toCommand(request, courseId, currentUserId);

        Certificate certificate = manageCertificateTemplateUseCase.upsertCertificateTemplate(command);

        return ResponseEntity.ok(restMapper.toResponse(certificate));
    }
    @GetMapping("/{courseId}/certificate-template")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CertificateTemplateResponse> getCertificateTemplate(
            @PathVariable Long courseId) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        Certificate certificate = manageCertificateTemplateUseCase.getCertificateTemplate(courseId, currentUserId);
        return ResponseEntity.ok(restMapper.toResponse(certificate));
    }

    @DeleteMapping("/{courseId}/certificate-template")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> deleteCertificateTemplate(
            @PathVariable Long courseId) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        manageCertificateTemplateUseCase.deleteCertificateTemplate(courseId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}