package com.lms.enrollmentservice.application.service;

import com.lms.enrollmentservice.application.port.in.command.EnrollCommand;
import com.lms.enrollmentservice.application.port.in.command.TrackProgressCommand;
import com.lms.enrollmentservice.application.port.out.CourseReferenceRepositoryPort;
import com.lms.enrollmentservice.application.port.out.CourseSummaryPort;
import com.lms.enrollmentservice.application.port.out.CourseValidationPort;
import com.lms.enrollmentservice.application.port.out.EnrollmentRepositoryPort;
import com.lms.enrollmentservice.application.port.out.dto.CourseSummary;
import com.lms.enrollmentservice.domain.enums.EnrollmentStatus;
import com.lms.enrollmentservice.domain.model.CourseReference;
import com.lms.enrollmentservice.domain.model.Enrollment;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentApplicationServiceTest {

    @Mock
    private EnrollmentRepositoryPort enrollmentRepository;

    @Mock
    private CourseReferenceRepositoryPort courseReferenceRepository;

    @Mock
    private CertificateApplicationService certificateApplicationService;

    // Thêm 2 Mock Port mới bị thiếu
    @Mock
    private CourseSummaryPort courseSummaryPort;

    @Mock
    private CourseValidationPort courseValidationPort;

    @InjectMocks
    private EnrollmentApplicationService enrollmentApplicationService;

    // =========================================================================================
    // PHẦN 1: TEST KỊCH BẢN GHI DANH (ENROLLMENT FLOW - MỚI THEO E1/E2)
    // =========================================================================================

    @Test
    @DisplayName("Nên ném Exception khi ghi danh nhưng khóa học không tồn tại (COURSE_NOT_FOUND)")
    void shouldThrowExceptionWhenEnrollingAndCourseNotFound() {
        EnrollCommand command = new EnrollCommand("student-1", 99L);
        when(courseReferenceRepository.findByCourseId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentApplicationService.enrollUser(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COURSE_NOT_FOUND);
    }

    @Test
    @DisplayName("Nên ném Exception khi khóa học chưa PUBLISHED (DRAFT, ARCHIVED...)")
    void shouldThrowExceptionWhenEnrollingAndCourseNotPublished() {
        EnrollCommand command = new EnrollCommand("student-1", 1L);
        CourseReference reference = CourseReference.builder().courseId(1L).instructorId("inst-1").build();
        CourseSummary summary = new CourseSummary(1L, "Test Course", "DRAFT");

        when(courseReferenceRepository.findByCourseId(1L)).thenReturn(Optional.of(reference));
        when(courseSummaryPort.getCourseSummary(1L)).thenReturn(summary);

        assertThatThrownBy(() -> enrollmentApplicationService.enrollUser(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COURSE_INVALID_STATUS);
    }

    @Test
    @DisplayName("Nên ném Exception khi học viên đã ghi danh (Duplicate Enrollment)")
    void shouldThrowExceptionWhenEnrollingDuplicate() {
        EnrollCommand command = new EnrollCommand("student-1", 1L);
        CourseReference reference = CourseReference.builder().courseId(1L).instructorId("inst-1").build();
        CourseSummary summary = new CourseSummary(1L, "Test Course", "PUBLISHED");

        when(courseReferenceRepository.findByCourseId(1L)).thenReturn(Optional.of(reference));
        when(courseSummaryPort.getCourseSummary(1L)).thenReturn(summary);
        when(enrollmentRepository.existsByUserIdAndCourseId("student-1", 1L)).thenReturn(true);

        assertThatThrownBy(() -> enrollmentApplicationService.enrollUser(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ENROLLMENT_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Nên ném Exception khi Giảng viên tự ghi danh vào khóa học của chính mình")
    void shouldThrowExceptionWhenInstructorEnrollsInOwnCourse() {
        EnrollCommand command = new EnrollCommand("inst-1", 1L);
        CourseReference reference = CourseReference.builder().courseId(1L).instructorId("inst-1").build();
        CourseSummary summary = new CourseSummary(1L, "Test Course", "PUBLISHED");

        when(courseReferenceRepository.findByCourseId(1L)).thenReturn(Optional.of(reference));
        when(courseSummaryPort.getCourseSummary(1L)).thenReturn(summary);
        when(enrollmentRepository.existsByUserIdAndCourseId("inst-1", 1L)).thenReturn(false);

        assertThatThrownBy(() -> enrollmentApplicationService.enrollUser(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
    }

    @Test
    @DisplayName("Nên ghi danh thành công với trạng thái ACTIVE")
    void shouldEnrollUserSuccessfully() {
        EnrollCommand command = new EnrollCommand("student-1", 1L);
        CourseReference reference = CourseReference.builder().courseId(1L).instructorId("inst-1").build();
        CourseSummary summary = new CourseSummary(1L, "Test Course", "PUBLISHED");

        when(courseReferenceRepository.findByCourseId(1L)).thenReturn(Optional.of(reference));
        when(courseSummaryPort.getCourseSummary(1L)).thenReturn(summary);
        when(enrollmentRepository.existsByUserIdAndCourseId("student-1", 1L)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        Enrollment result = enrollmentApplicationService.enrollUser(command);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(result.getUserId()).isEqualTo("student-1");
    }

    // =========================================================================================
    // PHẦN 2: TEST KỊCH BẢN TIẾN ĐỘ (LEARNING FLOW - CŨ ĐƯỢC CẬP NHẬT)
    // =========================================================================================

    @Test
    @DisplayName("Nên ném Exception khi track progress nhưng Enrollment đã bị CANCELLED")
    void shouldThrowExceptionWhenTrackingProgressAndEnrollmentCancelled() {
        TrackProgressCommand command = new TrackProgressCommand("student-1", 1L, 101L, 300, true);
        Enrollment mockEnrollment = Enrollment.builder().status(EnrollmentStatus.CANCELLED).build();

        when(enrollmentRepository.findByUserIdAndCourseId("student-1", 1L)).thenReturn(Optional.of(mockEnrollment));

        // Hàm checkCanTrackProgress() trong Domain sẽ throw ENROLLMENT_NOT_ACTIVE
        assertThatThrownBy(() -> enrollmentApplicationService.trackLessonProgress(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ENROLLMENT_NOT_ACTIVE);
    }

    @Test
    @DisplayName("Nên ném Exception khi Lesson không hợp lệ từ course-service")
    void shouldThrowExceptionWhenTrackingProgressAndLessonInvalid() {
        TrackProgressCommand command = new TrackProgressCommand("student-1", 1L, 101L, 300, true);
        Enrollment mockEnrollment = Enrollment.builder().status(EnrollmentStatus.ACTIVE).build();

        when(enrollmentRepository.findByUserIdAndCourseId("student-1", 1L)).thenReturn(Optional.of(mockEnrollment));
        when(courseValidationPort.isLessonValidForCourse(1L, 101L)).thenReturn(false); // Invalid Lesson

        assertThatThrownBy(() -> enrollmentApplicationService.trackLessonProgress(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LESSON_NOT_FOUND);
    }

    @Test
    @DisplayName("1. Nên cập nhật tiến độ và giữ trạng thái ACTIVE khi chưa hoàn thành 100%")
    void shouldTrackProgressAndStayActive() {
        String userId = "student-1";
        Long courseId = 1L;
        TrackProgressCommand command = new TrackProgressCommand(userId, courseId, 101L, 300, true);

        Enrollment mockEnrollment = Enrollment.builder().id(1L).userId(userId).courseId(courseId).status(EnrollmentStatus.ACTIVE).build();
        CourseReference mockReference = CourseReference.builder().courseId(courseId).totalLessons(2).build();

        when(enrollmentRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(mockEnrollment));
        when(courseValidationPort.isLessonValidForCourse(courseId, 101L)).thenReturn(true); // Fix NPE
        when(courseReferenceRepository.findByCourseId(courseId)).thenReturn(Optional.of(mockReference));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(mockEnrollment);

        Enrollment result = enrollmentApplicationService.trackLessonProgress(command);

        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    @DisplayName("2. Nên chuyển sang COMPLETED và tự động kích hoạt cấp chứng chỉ khi đạt 100%")
    void shouldCompleteAndTriggerCertificateIssuance() {
        String userId = "student-1";
        Long courseId = 1L;
        TrackProgressCommand command = new TrackProgressCommand(userId, courseId, 101L, 300, true);

        Enrollment mockEnrollment = Enrollment.builder().id(1L).userId(userId).courseId(courseId).status(EnrollmentStatus.ACTIVE).build();
        CourseReference mockReference = CourseReference.builder().courseId(courseId).totalLessons(1).build();

        when(enrollmentRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(mockEnrollment));
        when(courseValidationPort.isLessonValidForCourse(courseId, 101L)).thenReturn(true); // Fix NPE
        when(courseReferenceRepository.findByCourseId(courseId)).thenReturn(Optional.of(mockReference));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(mockEnrollment);

        Enrollment result = enrollmentApplicationService.trackLessonProgress(command);

        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        verify(certificateApplicationService, times(1)).issueCertificateForEnrollment(1L);
    }

    @Test
    @DisplayName("3. Nên ném BusinessException khi không tìm thấy CourseReference")
    void shouldThrowExceptionWhenCourseReferenceNotFound() {
        String userId = "student-1";
        Long courseId = 1L;
        TrackProgressCommand command = new TrackProgressCommand(userId, courseId, 101L, 300, true);

        Enrollment mockEnrollment = Enrollment.builder().id(1L).userId(userId).courseId(courseId).status(EnrollmentStatus.ACTIVE).build();

        when(enrollmentRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(mockEnrollment));
        when(courseValidationPort.isLessonValidForCourse(courseId, 101L)).thenReturn(true); // Đi qua bước validate
        when(courseReferenceRepository.findByCourseId(courseId)).thenReturn(Optional.empty()); // Fail ở bước tìm Course

        assertThatThrownBy(() -> enrollmentApplicationService.trackLessonProgress(command))
                .isInstanceOf(BusinessException.class)
                // Đổi thành COURSE_NOT_FOUND (hoặc COURSE_REFERENCE_NOT_FOUND tùy ErrorCode hiện tại của bạn)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COURSE_NOT_FOUND);
    }

    @Test
    @DisplayName("4. Nên tính toán đúng 50% tiến độ khi hoàn thành 1 trong 2 bài")
    void shouldCalculateFiftyPercentProgressAccurately() {
        TrackProgressCommand command = new TrackProgressCommand("student-1", 1L, 101L, 300, true);
        Enrollment mockEnrollment = Enrollment.builder().id(1L).userId("student-1").courseId(1L).status(EnrollmentStatus.ACTIVE).build();
        CourseReference mockReference = CourseReference.builder().courseId(1L).totalLessons(2).build();

        when(enrollmentRepository.findByUserIdAndCourseId("student-1", 1L)).thenReturn(Optional.of(mockEnrollment));
        when(courseValidationPort.isLessonValidForCourse(1L, 101L)).thenReturn(true);
        when(courseReferenceRepository.findByCourseId(1L)).thenReturn(Optional.of(mockReference));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(mockEnrollment);

        Enrollment result = enrollmentApplicationService.trackLessonProgress(command);

        assertThat(result.getProgressPercentage()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("5. Nên sử dụng ArgumentCaptor để kiểm tra đúng dữ liệu Enrollment được lưu")
    void shouldCaptureAndVerifySavedEnrollmentData() {
        TrackProgressCommand command = new TrackProgressCommand("student-1", 1L, 101L, 300, true);
        Enrollment mockEnrollment = Enrollment.builder().id(1L).userId("student-1").courseId(1L).status(EnrollmentStatus.ACTIVE).build();
        CourseReference mockReference = CourseReference.builder().courseId(1L).totalLessons(1).build();

        when(enrollmentRepository.findByUserIdAndCourseId("student-1", 1L)).thenReturn(Optional.of(mockEnrollment));
        when(courseValidationPort.isLessonValidForCourse(1L, 101L)).thenReturn(true);
        when(courseReferenceRepository.findByCourseId(1L)).thenReturn(Optional.of(mockReference));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        enrollmentApplicationService.trackLessonProgress(command);

        ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    }
}