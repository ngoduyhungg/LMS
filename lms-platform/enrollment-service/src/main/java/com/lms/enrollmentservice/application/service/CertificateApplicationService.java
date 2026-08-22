package com.lms.enrollmentservice.application.service;

import com.lms.enrollmentservice.application.port.in.GetCertificateUseCase;
import com.lms.enrollmentservice.application.port.in.ManageCertificateTemplateUseCase;
import com.lms.enrollmentservice.application.port.in.ManageCertificateUseCase;
import com.lms.enrollmentservice.application.port.in.ResetDevCertificateUseCase;
import com.lms.enrollmentservice.application.port.in.command.UpsertCertificateCommand;
import com.lms.enrollmentservice.application.port.out.*;
import com.lms.enrollmentservice.application.port.out.dto.CertificateDocumentModel;
import com.lms.enrollmentservice.application.port.out.dto.CourseSummary;
import com.lms.enrollmentservice.application.port.out.dto.UserProfile;
import com.lms.enrollmentservice.domain.enums.EnrollmentStatus;
import com.lms.enrollmentservice.domain.model.Certificate;
import com.lms.enrollmentservice.domain.model.CourseReference;
import com.lms.enrollmentservice.domain.model.Enrollment;
import com.lms.enrollmentservice.domain.model.UserCertificate;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateApplicationService implements ManageCertificateUseCase, GetCertificateUseCase, ManageCertificateTemplateUseCase, ResetDevCertificateUseCase {

    private final UserCertificateRepositoryPort userCertificateRepository;
    private final CertificateRepositoryPort certificateRepository;
    private final EnrollmentRepositoryPort enrollmentRepository;

    private final CourseReferenceRepositoryPort courseReferenceRepository;
    private final CourseSummaryPort courseSummaryPort;
    private final UserProfilePort userProfilePort;
    private final PdfGeneratorPort pdfGeneratorPort;
    private final FileStoragePort fileStoragePort;

    @Override
    public UserCertificate issueCertificateForEnrollment(Long enrollmentId) {
        // 1. Idempotency Check
        if (userCertificateRepository.existsByEnrollmentId(enrollmentId)) {
            return userCertificateRepository.findByEnrollmentId(enrollmentId).get();
        }

        // 2 & 3. Validate Enrollment
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.ENROLLMENT_NOT_ACTIVE);
        }

        // 4. Validate Certificate Template
        java.util.Optional<Certificate> templateOpt = certificateRepository.findByCourseId(enrollment.getCourseId());
        if (templateOpt.isEmpty()) {
            return null;
        }
        Certificate certificateTemplate = templateOpt.get();

        // 5 & 6. Load CourseSummary & Validate Status
        CourseSummary summary = courseSummaryPort.getCourseSummary(enrollment.getCourseId());
        if ("DRAFT".equals(summary.status())) {
            throw new BusinessException(ErrorCode.COURSE_INVALID_STATUS);
        }

        // 7. Get UserProfile
        UserProfile profile = userProfilePort.getProfile(enrollment.getUserId());
        String fullName = profile.fullName();

        // 8 & 9. Generate Certificate Code & Issue Date (Source of truth)
        String certCode = UserCertificate.generateCode();
        ZonedDateTime issuedAtZoned = ZonedDateTime.now();
        LocalDate issueDate = issuedAtZoned.toLocalDate(); // Map cho PDF

        // 10. Build CertificateDocumentModel
        CertificateDocumentModel model = new CertificateDocumentModel(
                fullName,
                summary.title(),
                certCode,
                issueDate
        );

        // 11. Generate PDF
        byte[] pdfBytes = pdfGeneratorPort.generateCertificate(model);

        // 12. Determine Filename & Upload to Storage
        String fileName = String.format("certificate_%d_%s_%s.pdf",
                enrollment.getCourseId(), enrollment.getUserId(), certCode);

        String realPdfUrl = fileStoragePort.uploadFile(fileName, pdfBytes, "application/pdf");

        // 13 & 14. Issue New Certificate (Sử dụng đúng certCode và issuedAtZoned)
        UserCertificate newUserCert = UserCertificate.issueNew(
                certificateTemplate.getId(),
                enrollment.getUserId(),
                enrollment.getId(),
                realPdfUrl,
                certCode,
                issuedAtZoned
        );

        return userCertificateRepository.save(newUserCert);
    }

    @Override
    public Certificate upsertCertificateTemplate(UpsertCertificateCommand command) {
        // 1. Lấy CourseReference, ném COURSE_NOT_FOUND nếu không có
        CourseReference reference = courseReferenceRepository.findByCourseId(command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        // 2. Kiểm tra Ownership (IDOR Protection)
        if (!reference.getInstructorId().equals(command.currentUserId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 3. Upsert Logic
        Certificate certificate = certificateRepository.findByCourseId(command.courseId())
                .orElseGet(() -> Certificate.builder()
                        .courseId(command.courseId())
                        .build());

        certificate.updateTemplate(command.title(), command.templateUrl());
        return certificateRepository.save(certificate);
    }
    @Override
    @Transactional(readOnly = true)
    public List<UserCertificate> getMyCertificates(String userId) {
        return userCertificateRepository.findByUserId(userId);
    }
    @Override
    @Transactional
    public void resetCertificateByEnrollmentId(Long enrollmentId) {
        // 1. Xác minh Enrollment tồn tại (Xử lý theo business convention)
        enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        // 2. Tìm và Xóa Certificate (Idempotent: Không có thì thôi, không báo lỗi)
        // 3. KHÔNG gọi S3/MinIO xóa file PDF (Chấp nhận orphan file trong Dev)
        if (userCertificateRepository.existsByEnrollmentId(enrollmentId)) {
            userCertificateRepository.deleteByEnrollmentId(enrollmentId);
        }
    }
}
