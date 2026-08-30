package com.lms.enrollmentservice.application.service;

import com.lms.enrollmentservice.adapter.in.rest.dto.AdminCourseEnrollmentSummaryResponse;
import com.lms.enrollmentservice.adapter.in.rest.dto.AdminStudentEnrollmentResponse;
import com.lms.enrollmentservice.adapter.in.rest.dto.PageResponse;
import com.lms.enrollmentservice.application.port.in.GetEnrollmentUseCase;
import com.lms.enrollmentservice.application.port.in.ManageAdminEnrollmentUseCase;
import com.lms.enrollmentservice.application.port.in.ManageEnrollmentUseCase;
import com.lms.enrollmentservice.application.port.in.command.EnrollCommand;
import com.lms.enrollmentservice.application.port.in.command.TrackProgressCommand;
import com.lms.enrollmentservice.application.port.out.*;
import com.lms.enrollmentservice.application.port.out.dto.CourseEnrollmentAggregation;
import com.lms.enrollmentservice.application.port.out.dto.CourseSummary;
import com.lms.enrollmentservice.application.port.out.dto.UserProfile;
import com.lms.enrollmentservice.domain.enums.EnrollmentStatus;
import com.lms.enrollmentservice.domain.model.CourseReference;
import com.lms.enrollmentservice.domain.model.Enrollment;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentApplicationService implements ManageEnrollmentUseCase, GetEnrollmentUseCase, ManageAdminEnrollmentUseCase {

    private final EnrollmentRepositoryPort enrollmentRepository;
    private final CourseReferenceRepositoryPort courseReferenceRepositoryPort;
    private final CertificateApplicationService certificateApplicationService;

    private final CourseSummaryPort courseSummaryPort;
    private final CourseValidationPort courseValidationPort;
    private final UserProfilePort userProfilePort;

    @Override
    public Enrollment enrollUser(EnrollCommand command) {
        // 1. Kiểm tra CourseReference (Chặn dữ liệu ma)
        CourseReference reference = courseReferenceRepositoryPort.findByCourseId(command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        // 2. Lấy Status realtime từ course-service
        CourseSummary summary = courseSummaryPort.getCourseSummary(command.courseId());
        if (!"PUBLISHED".equals(summary.status())) {
            throw new BusinessException(ErrorCode.COURSE_INVALID_STATUS);
        }

        // 3. Kiểm tra duplicate
        if (enrollmentRepository.existsByUserIdAndCourseId(command.userId(), command.courseId())) {
            throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }

        // 4. Kiểm tra Instructor tự ghi danh (Self-enrollment)
        if (command.userId().equals(reference.getInstructorId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 5. Tạo mới
        Enrollment newEnrollment = Enrollment.builder()
                .userId(command.userId())
                .courseId(command.courseId())
                .status(EnrollmentStatus.ACTIVE)
                .enrolledAt(ZonedDateTime.now())
                .build();

        return enrollmentRepository.save(newEnrollment);
    }

    @Override
    public Enrollment trackLessonProgress(TrackProgressCommand command) {
        // 1. Kéo Aggregate Root Enrollment lên (Đảm bảo Ownership qua userId + courseId)
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(command.userId(), command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        // 2. Domain tự kiểm tra trạng thái
        enrollment.checkCanTrackProgress();

        // 3. Gọi HTTP sang course-service để validate lesson
        boolean isLessonValid = courseValidationPort.isLessonValidForCourse(command.courseId(), command.lessonId());
        if (!isLessonValid) {
            throw new BusinessException(ErrorCode.LESSON_NOT_FOUND);
        }

        // 4. Kéo Projection CourseReference lên để lấy tổng số bài học hiện hành
        CourseReference reference = courseReferenceRepositoryPort.findByCourseId(command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        // 5. Delegate business logic cho Domain
        enrollment.recordLessonProgress(
                command.lessonId(),
                command.watchedSeconds(),
                command.isCompleted(),
                reference.getTotalLessons()
        );

        // 6. Lưu lại
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // 7. Kiểm tra chứng chỉ
        if (savedEnrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            certificateApplicationService.issueCertificateForEnrollment(savedEnrollment.getId());
        }

        return savedEnrollment;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> getMyEnrollments(String userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Enrollment getEnrollmentDetail(String userId, Long courseId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));
    }
    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    @Transactional
    public Enrollment forceCancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        enrollment.cancel();

        return enrollmentRepository.save(enrollment);
    }
    @Override
    public List<AdminCourseEnrollmentSummaryResponse> getCourseEnrollmentSummaries() {
        // 1. Lấy dữ liệu đếm từ DB
        List<CourseEnrollmentAggregation> aggregations = enrollmentRepository.getCourseEnrollmentAggregations();
        if (aggregations.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Tách list ID để gọi batch API
        List<Long> courseIds = aggregations.stream()
                .map(CourseEnrollmentAggregation::courseId)
                .toList();

        // 3. Gọi external service ĐÚNG 1 LẦN và đẩy vào Map để truy xuất O(1)
        Map<Long, CourseSummary> courseSummaryMap = courseSummaryPort.getCourseSummaries(courseIds)
                .stream()
                .collect(Collectors.toMap(CourseSummary::courseId, summary -> summary));

        // 4. Map DTO
        return aggregations.stream().map(agg -> {
            CourseSummary summary = courseSummaryMap.get(agg.courseId());
            return AdminCourseEnrollmentSummaryResponse.builder()
                    .courseId(agg.courseId())
                    .enrollmentCount(agg.enrollmentCount())
                    .activeCount(agg.activeCount())
                    .completedCount(agg.completedCount())
                    .courseTitle(summary != null ? summary.title() : null)
                    .courseStatus(summary != null ? summary.status() : null)
                    .build();
        }).toList();
    }
    @Override
    public PageResponse<AdminStudentEnrollmentResponse> getStudentEnrollmentsByCourse(Long courseId, int page, int size) {
        // 1. Lấy dữ liệu phân trang từ DB
        Page<Enrollment> enrollmentPage = enrollmentRepository.findByCourseId(courseId, PageRequest.of(page, size));

        if (enrollmentPage.isEmpty()) {
            return new PageResponse<>(Collections.emptyList(), page, size, enrollmentPage.getTotalElements());
        }

        // 2. Gom userIds duy nhất
        List<String> userIds = enrollmentPage.getContent().stream()
                .map(Enrollment::getUserId) // Hoặc userId() tùy định nghĩa model của bạn
                .distinct()
                .toList();

        // 3. Gọi batch API lấy thông tin người dùng 1 lần
        Map<String, UserProfile> userProfileMap = userProfilePort.getProfiles(userIds).stream()
                .collect(Collectors.toMap(UserProfile::userId, p -> p));

        // 4. Map kết quả
        List<AdminStudentEnrollmentResponse> items = enrollmentPage.getContent().stream().map(enrollment -> {
            BigDecimal actualProgress = (enrollment.getStatus() == EnrollmentStatus.COMPLETED)
                    ? BigDecimal.valueOf(100)
                    : enrollment.getProgressPercentage();
            UserProfile profile = userProfileMap.get(enrollment.getUserId());
            return AdminStudentEnrollmentResponse.builder()
                    .enrollmentId(enrollment.getId())
                    .studentId(enrollment.getUserId())
                    .studentName(profile != null ? profile.fullName() : null)
                    .studentEmail(profile != null ? profile.email() : null)
                    .status(enrollment.getStatus() != null ? enrollment.getStatus().name() : null)
                    .progressPercentage(actualProgress)
                    .lastAccessedLessonId(enrollment.getLastAccessedLessonId())
                    .enrolledAt(enrollment.getEnrolledAt())
                    .completedAt(enrollment.getCompletedAt())
                    .build();
        }).toList();

        return new PageResponse<>(items, page, size, enrollmentPage.getTotalElements());
    }
}
