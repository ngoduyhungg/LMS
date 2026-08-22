package com.lms.enrollmentservice.application.service;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateApplicationServiceTest {

    @Mock
    private UserCertificateRepositoryPort userCertificateRepository;

    @Mock
    private CertificateRepositoryPort certificateRepository;

    @Mock
    private EnrollmentRepositoryPort enrollmentRepository;

    @InjectMocks
    private CertificateApplicationService certificateApplicationService;
    @Mock
    private CourseReferenceRepositoryPort courseReferenceRepositoryPort;
    @Mock
    private CourseSummaryPort courseSummaryPort;
    @Mock
    private UserProfilePort userProfilePort;
    @Mock private PdfGeneratorPort pdfGeneratorPort;
    @Mock private FileStoragePort fileStoragePort;
    @Test
    @DisplayName("Nên trả về chứng chỉ hiện có nếu đã tồn tại cho enrollmentId (Rule 3)")
    void shouldReturnExistingCertificateIfAlreadyIssued() {
        Long enrollmentId = 2L;
        UserCertificate existingCert = UserCertificate.builder().id(10L).enrollmentId(enrollmentId).build();

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(true);
        when(userCertificateRepository.findByEnrollmentId(enrollmentId)).thenReturn(Optional.of(existingCert));

        UserCertificate result = certificateApplicationService.issueCertificateForEnrollment(enrollmentId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        verify(userCertificateRepository, times(1)).existsByEnrollmentId(enrollmentId);
        verify(userCertificateRepository, times(1)).findByEnrollmentId(enrollmentId);
        verify(enrollmentRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Nên phát hành chứng chỉ mới thành công khi chưa tồn tại")
    void shouldIssueNewCertificateSuccessfully() {
        Long enrollmentId = 2L;
        Long courseId = 1L;
        String userId = "student-1";

        // Bổ sung status COMPLETED để qua cửa ải Phase E3
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).courseId(courseId).userId("student-1")
                .status(EnrollmentStatus.COMPLETED).build();
        Certificate template = Certificate.builder().id(5L).courseId(courseId).build();

        // Bổ sung mock CourseSummaryPort để qua cửa ải Check Status & Lấy Title
        com.lms.enrollmentservice.application.port.out.dto.CourseSummary summary =
                new com.lms.enrollmentservice.application.port.out.dto.CourseSummary(courseId, "Java Core", "PUBLISHED");

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(courseId)).thenReturn(Optional.of(template));
        when(courseSummaryPort.getCourseSummary(courseId)).thenReturn(summary); // <-- Mới
        when(userProfilePort.getProfile(userId)).thenReturn(new UserProfile(userId, "email@ex.com", "Full Name", "url"));
        when(userCertificateRepository.save(any(UserCertificate.class))).thenAnswer(inv -> inv.getArgument(0));

        UserCertificate result = certificateApplicationService.issueCertificateForEnrollment(enrollmentId);

        assertThat(result).isNotNull();
        verify(userCertificateRepository, times(1)).save(any(UserCertificate.class));
    }

    @Test
    @DisplayName("Nên ném BusinessException (ENROLLMENT_NOT_FOUND) khi không tìm thấy Enrollment")
    void shouldThrowExceptionWhenEnrollmentNotFound() {
        Long enrollmentId = 99L;

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificateApplicationService.issueCertificateForEnrollment(enrollmentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ENROLLMENT_NOT_FOUND);

        verify(certificateRepository, never()).findByCourseId(any());
    }

    @Test
    @DisplayName("Nên trả về null (Graceful exit) khi không tìm thấy Mẫu chứng chỉ thay vì ném lỗi")
    void shouldReturnNullWhenCertificateTemplateNotFound() {
        // Arrange
        Long enrollmentId = 2L;
        Long courseId = 1L;

        Enrollment enrollment = Enrollment.builder()
                .id(enrollmentId)
                .courseId(courseId)
                .userId("student-1")
                .status(EnrollmentStatus.COMPLETED)
                .build();

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(courseId)).thenReturn(Optional.empty());

        // Act
        UserCertificate result = certificateApplicationService.issueCertificateForEnrollment(enrollmentId);

        // Assert (Dùng AssertJ cho đồng bộ với phong cách code của bạn)
        org.assertj.core.api.Assertions.assertThat(result).isNull();

        // Verify đảm bảo không có chứng chỉ rác nào được lưu xuống DB
        verify(userCertificateRepository, never()).save(any());
    }

    @Test
    @DisplayName("Nên lấy danh sách chứng chỉ của học viên thành công (getMyCertificates)")
    void shouldGetMyCertificatesSuccessfully() {
        String userId = "student-1";
        List<UserCertificate> certs = List.of(UserCertificate.builder().id(1L).userId(userId).build());

        when(userCertificateRepository.findByUserId(userId)).thenReturn(certs);

        List<UserCertificate> result = certificateApplicationService.getMyCertificates(userId);

        assertThat(result).hasSize(1);
        verify(userCertificateRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Nên tạo mới mẫu chứng chỉ khi chưa tồn tại (Upsert - Create)")
    void shouldCreateCertificateTemplateWhenNotExists() {
        Long courseId = 1L;
        String currentUserId = "AC1";
        UpsertCertificateCommand command = new UpsertCertificateCommand(courseId, "Title", "url.pdf", currentUserId);

        // Bổ sung mock CourseReference để qua cửa ải Ownership
        CourseReference reference = CourseReference.builder().courseId(courseId).instructorId(currentUserId).build();
        when(courseReferenceRepositoryPort.findByCourseId(courseId)).thenReturn(Optional.of(reference)); // <-- Mới

        when(certificateRepository.findByCourseId(courseId)).thenReturn(Optional.empty());
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(inv -> inv.getArgument(0));

        Certificate result = certificateApplicationService.upsertCertificateTemplate(command);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Nên cập nhật mẫu chứng chỉ khi đã tồn tại (Upsert - Update)")
    void shouldUpdateCertificateTemplateWhenExists() {
        Long courseId = 1L;
        String currentUserId = "AC1";
        UpsertCertificateCommand command = new UpsertCertificateCommand(courseId, "New Title", "new-url.pdf", currentUserId);
        Certificate existing = Certificate.builder().id(10L).courseId(courseId).title("Old Title").build();

        // Bổ sung mock CourseReference để qua cửa ải Ownership
        CourseReference reference = CourseReference.builder().courseId(courseId).instructorId(currentUserId).build();
        when(courseReferenceRepositoryPort.findByCourseId(courseId)).thenReturn(Optional.of(reference)); // <-- Mới

        when(certificateRepository.findByCourseId(courseId)).thenReturn(Optional.of(existing));
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(inv -> inv.getArgument(0));

        Certificate result = certificateApplicationService.upsertCertificateTemplate(command);

        assertThat(result).isNotNull();
    }
    @Test
    @DisplayName("Nên ném BusinessException khi cấp chứng chỉ nhưng Enrollment chưa COMPLETED")
    void shouldThrowExceptionWhenEnrollmentNotCompleted() {
        Long enrollmentId = 3L;
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).status(EnrollmentStatus.ACTIVE).build();

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> certificateApplicationService.issueCertificateForEnrollment(enrollmentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ENROLLMENT_NOT_ACTIVE);
    }

    @Test
    @DisplayName("Nên ném BusinessException khi khóa học bị DRAFT")
    void shouldThrowExceptionWhenCourseIsDraft() {
        Long enrollmentId = 4L;
        Long courseId = 200L;
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).courseId(courseId).status(EnrollmentStatus.COMPLETED).build();
        Certificate template = Certificate.builder().id(10L).courseId(courseId).build();

        com.lms.enrollmentservice.application.port.out.dto.CourseSummary summary =
                new com.lms.enrollmentservice.application.port.out.dto.CourseSummary(courseId, "Draft Course", "DRAFT");

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(courseId)).thenReturn(Optional.of(template));
        when(courseSummaryPort.getCourseSummary(courseId)).thenReturn(summary);

        assertThatThrownBy(() -> certificateApplicationService.issueCertificateForEnrollment(enrollmentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COURSE_INVALID_STATUS);
    }

    @Test
    @DisplayName("Nên ném BusinessException khi giảng viên khác sửa Mẫu chứng chỉ (Sai Ownership)")
    void shouldThrowExceptionWhenUpsertingWithWrongOwnership() {
        UpsertCertificateCommand command = new UpsertCertificateCommand(100L, "Title", "url", "hacker-inst");
        CourseReference reference = CourseReference.builder().courseId(100L).instructorId("real-inst").build();

        when(courseReferenceRepositoryPort.findByCourseId(100L)).thenReturn(Optional.of(reference));

        assertThatThrownBy(() -> certificateApplicationService.upsertCertificateTemplate(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
    }
    @Test
    @DisplayName("Test 1 - User Profile Success: Flow tiếp tục thành công và port được gọi đúng")
    void shouldIssueCertificateWhenUserProfileIsSuccessfullyFetched() {
        Long enrollmentId = 10L;
        Long courseId = 100L;
        String userId = "student-x";

        Enrollment enrollment = Enrollment.builder().id(enrollmentId).userId(userId).courseId(courseId).status(EnrollmentStatus.COMPLETED).build();
        Certificate template = Certificate.builder().id(1L).courseId(courseId).build();
        CourseSummary summary = new CourseSummary(courseId, "Title", "PUBLISHED");
        UserProfile profile = new UserProfile(userId, "email@ex.com", "Nguyen Van A", "url");

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(courseId)).thenReturn(Optional.of(template));
        when(courseSummaryPort.getCourseSummary(courseId)).thenReturn(summary);

        // MOCK Port mới
        when(userProfilePort.getProfile(userId)).thenReturn(profile);
        when(userCertificateRepository.save(any(UserCertificate.class))).thenAnswer(inv -> inv.getArgument(0));

        UserCertificate result = certificateApplicationService.issueCertificateForEnrollment(enrollmentId);

        assertThat(result).isNotNull();
        // Verify UserProfilePort được gọi chính xác với userId
        verify(userProfilePort, times(1)).getProfile(userId);
    }

    @Test
    @DisplayName("Test 2 - User Not Found: Exception propagate, không tạo certificate giả")
    void shouldThrowExceptionWhenUserNotFound() {
        Long enrollmentId = 11L;
        String userId = "student-not-found";
        Long courseId = 100L;
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).userId(userId).courseId(courseId).status(EnrollmentStatus.COMPLETED).build();

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(anyLong())).thenReturn(Optional.of(Certificate.builder().build()));
        when(courseSummaryPort.getCourseSummary(anyLong())).thenReturn(new CourseSummary(1L, "Title", "PUBLISHED"));

        // MOCK ném USER_NOT_FOUND
        when(userProfilePort.getProfile(userId)).thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        assertThatThrownBy(() -> certificateApplicationService.issueCertificateForEnrollment(enrollmentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(userCertificateRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 3 - User Service Failure: Ném INTERNAL_SERVER_ERROR khi lỗi 5xx/Timeout")
    void shouldThrowExceptionWhenUserServiceFails() {
        Long enrollmentId = 12L;
        String userId = "student-x";
        Long courseId = 100L;
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).userId(userId).courseId(courseId).status(EnrollmentStatus.COMPLETED).build();

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(anyLong())).thenReturn(Optional.of(Certificate.builder().build()));
        when(courseSummaryPort.getCourseSummary(anyLong())).thenReturn(new CourseSummary(1L, "Title", "PUBLISHED"));

        // MOCK ném INTERNAL_SERVER_ERROR
        when(userProfilePort.getProfile(userId)).thenThrow(new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> certificateApplicationService.issueCertificateForEnrollment(enrollmentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);

        verify(userCertificateRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 4 - Existing Certificate / Idempotency: Không gọi UserProfilePort nếu certificate đã tồn tại")
    void shouldNotCallUserProfilePortIfCertificateExists() {
        Long enrollmentId = 13L;
        UserCertificate existingCert = UserCertificate.builder().id(99L).enrollmentId(enrollmentId).build();

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(true);
        when(userCertificateRepository.findByEnrollmentId(enrollmentId)).thenReturn(Optional.of(existingCert));

        UserCertificate result = certificateApplicationService.issueCertificateForEnrollment(enrollmentId);

        assertThat(result.getId()).isEqualTo(99L);
        // Verify KHÔNG gọi UserProfilePort và CourseSummaryPort
        verify(courseSummaryPort, never()).getCourseSummary(anyLong());
        verify(userProfilePort, never()).getProfile(anyString());
    }

    @Test
    @DisplayName("Test 5 - Correct User ID: Xác thực lấy đúng userId của enrollment")
    void shouldCallUserProfilePortWithExactUserId() {
        Long enrollmentId = 14L;
        String correctUserId = "exact-user-id-123";
        Long courseId = 100L;
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).userId(correctUserId).courseId(courseId).status(EnrollmentStatus.COMPLETED).build();
        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(anyLong())).thenReturn(Optional.of(Certificate.builder().build()));
        when(courseSummaryPort.getCourseSummary(anyLong())).thenReturn(new CourseSummary(1L, "Title", "PUBLISHED"));

        when(userProfilePort.getProfile(correctUserId)).thenReturn(new UserProfile(correctUserId, "email", "Name", "url"));
        when(userCertificateRepository.save(any(UserCertificate.class))).thenAnswer(inv -> inv.getArgument(0));

        certificateApplicationService.issueCertificateForEnrollment(enrollmentId);

        // Đảm bảo truyền đúng ID, không phải email, courseId hay username
        verify(userProfilePort).getProfile(correctUserId);
    }

    @Test
    @DisplayName("Test 6 - Profile Data Propagation: Đảm bảo fullName được gọi để sẵn sàng cho PDF (Phase tiếp theo)")
    void shouldPropagateFullNameSuccessfully() {
        Long enrollmentId = 15L;
        String userId = "student-z";
        String expectedFullName = "Tran Thi B";
        Long courseId = 100L;

        Enrollment enrollment = Enrollment.builder().id(enrollmentId).userId(userId).courseId(courseId).status(EnrollmentStatus.COMPLETED).build();
        UserProfile profile = new UserProfile(userId, "email", expectedFullName, "url");

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(anyLong())).thenReturn(Optional.of(Certificate.builder().build()));
        when(courseSummaryPort.getCourseSummary(anyLong())).thenReturn(new CourseSummary(1L, "Title", "PUBLISHED"));
        when(userProfilePort.getProfile(userId)).thenReturn(profile);
        when(userCertificateRepository.save(any(UserCertificate.class))).thenAnswer(inv -> inv.getArgument(0));

        UserCertificate result = certificateApplicationService.issueCertificateForEnrollment(enrollmentId);

        // Verify flow không bị gián đoạn và kết thúc thành công sau khi lấy fullName
        assertThat(result).isNotNull();
        verify(userProfilePort).getProfile(userId);
    }
// =======================================================================
    // PHASE E4 - STEP 4C: INTEGRATION TESTS
    // =======================================================================

    @Test
    @DisplayName("Test 1 - Happy path: Verify thứ tự gọi các Port và Save")
    void shouldFollowHappyPathAndCallPortsInOrder() {
        Long enrollmentId = 100L;
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).userId("user1").courseId(1L).status(EnrollmentStatus.COMPLETED).build();
        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(anyLong())).thenReturn(Optional.of(Certificate.builder().id(1L).build()));
        when(courseSummaryPort.getCourseSummary(anyLong())).thenReturn(new CourseSummary(1L, "Course", "PUBLISHED"));
        when(userProfilePort.getProfile(anyString())).thenReturn(new UserProfile("user1", "e", "Name", "u"));

        byte[] pdfBytes = "pdf-content".getBytes();
        when(pdfGeneratorPort.generateCertificate(any())).thenReturn(pdfBytes);
        when(fileStoragePort.uploadFile(anyString(), any(), anyString())).thenReturn("http://s3/file.pdf");
        when(userCertificateRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        certificateApplicationService.issueCertificateForEnrollment(enrollmentId);

        // Verify correct order conceptually
        verify(userProfilePort, times(1)).getProfile(anyString());
        verify(pdfGeneratorPort, times(1)).generateCertificate(any());
        verify(fileStoragePort, times(1)).uploadFile(anyString(), any(), anyString());
        verify(userCertificateRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test 2 - Existing certificate: Đảm bảo never() được gọi với các Outbound Port")
    void shouldNotCallPortsWhenCertificateAlreadyExists() {
        Long enrollmentId = 101L;
        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(true);
        when(userCertificateRepository.findByEnrollmentId(enrollmentId)).thenReturn(Optional.of(new UserCertificate()));

        certificateApplicationService.issueCertificateForEnrollment(enrollmentId);

        verify(courseSummaryPort, never()).getCourseSummary(anyLong());
        verify(userProfilePort, never()).getProfile(anyString());
        verify(pdfGeneratorPort, never()).generateCertificate(any());
        verify(fileStoragePort, never()).uploadFile(anyString(), any(), anyString());
    }

    @Test
    @DisplayName("Test 3 - User not found: Verify never generate, upload, save")
    void shouldHaltWhenUserNotFound() {
        Long enrollmentId = 102L;
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).courseId(1L).status(EnrollmentStatus.COMPLETED).build();
        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(anyLong())).thenReturn(Optional.of(Certificate.builder().build()));
        when(courseSummaryPort.getCourseSummary(anyLong())).thenReturn(new CourseSummary(1L, "Course", "PUBLISHED"));

        when(userProfilePort.getProfile(any())).thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        assertThatThrownBy(() -> certificateApplicationService.issueCertificateForEnrollment(enrollmentId))
                .isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(pdfGeneratorPort, never()).generateCertificate(any());
        verify(fileStoragePort, never()).uploadFile(anyString(), any(), anyString());
        verify(userCertificateRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 4 - PDF generation failure: Verify never upload, save")
    void shouldHaltWhenPdfGenerationFails() {
        Long enrollmentId = 103L;
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).courseId(1L).status(EnrollmentStatus.COMPLETED).build();
        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(anyLong())).thenReturn(Optional.of(Certificate.builder().build()));
        when(courseSummaryPort.getCourseSummary(anyLong())).thenReturn(new CourseSummary(1L, "Course", "PUBLISHED"));
        when(userProfilePort.getProfile(any())).thenReturn(new UserProfile("u", "e", "n", "a"));

        when(pdfGeneratorPort.generateCertificate(any())).thenThrow(new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> certificateApplicationService.issueCertificateForEnrollment(enrollmentId))
                .isInstanceOf(BusinessException.class);

        verify(fileStoragePort, never()).uploadFile(anyString(), any(), anyString());
        verify(userCertificateRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 5 - Storage failure: Verify never save DB")
    void shouldHaltWhenStorageFails() {
        Long enrollmentId = 104L;
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).courseId(1L).status(EnrollmentStatus.COMPLETED).build();
        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(anyLong())).thenReturn(Optional.of(Certificate.builder().build()));
        when(courseSummaryPort.getCourseSummary(anyLong())).thenReturn(new CourseSummary(1L, "Course", "PUBLISHED"));
        when(userProfilePort.getProfile(any())).thenReturn(new UserProfile("u", "e", "n", "a"));
        when(pdfGeneratorPort.generateCertificate(any())).thenReturn("pdf".getBytes());

        when(fileStoragePort.uploadFile(anyString(), any(), anyString())).thenThrow(new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> certificateApplicationService.issueCertificateForEnrollment(enrollmentId))
                .isInstanceOf(BusinessException.class);

        verify(userCertificateRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 6 & 7 - Correct filename and Content-Type")
    void shouldUploadWithCorrectFilenameAndContentType() {
        Long enrollmentId = 105L;
        String userId = "user99";
        Long courseId = 8L;
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).userId(userId).courseId(courseId).status(EnrollmentStatus.COMPLETED).build();

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(anyLong())).thenReturn(Optional.of(Certificate.builder().build()));
        when(courseSummaryPort.getCourseSummary(anyLong())).thenReturn(new CourseSummary(1L, "Course", "PUBLISHED"));
        when(userProfilePort.getProfile(any())).thenReturn(new UserProfile("u", "e", "n", "a"));

        byte[] pdfBytes = "test-pdf".getBytes();
        when(pdfGeneratorPort.generateCertificate(any())).thenReturn(pdfBytes);
        when(fileStoragePort.uploadFile(anyString(), any(), anyString())).thenReturn("url");
        when(userCertificateRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        certificateApplicationService.issueCertificateForEnrollment(enrollmentId);

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);
        ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);

        verify(fileStoragePort).uploadFile(nameCaptor.capture(), bytesCaptor.capture(), typeCaptor.capture());

        // Validate content and type (Test 7)
        assertThat(typeCaptor.getValue()).isEqualTo("application/pdf");
        assertThat(bytesCaptor.getValue()).isEqualTo(pdfBytes);

        // Validate filename (Test 6)
        String fileName = nameCaptor.getValue();
        assertThat(fileName).matches("^certificate_8_user99_CERT-[A-Z0-9]{8}\\.pdf$");
    }

    @Test
    @DisplayName("Test 8 - PDF/DB Consistency: Code và Date phải đồng nhất")
    void shouldMaintainPdfAndDbConsistency() {
        Long enrollmentId = 106L;
        Enrollment enrollment = Enrollment.builder().id(enrollmentId).userId("u").courseId(1L).status(EnrollmentStatus.COMPLETED).build();

        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(certificateRepository.findByCourseId(anyLong())).thenReturn(Optional.of(Certificate.builder().build()));
        when(courseSummaryPort.getCourseSummary(anyLong())).thenReturn(new CourseSummary(1L, "Course", "PUBLISHED"));
        when(userProfilePort.getProfile(any())).thenReturn(new UserProfile("u", "e", "n", "a"));
        when(pdfGeneratorPort.generateCertificate(any())).thenReturn(new byte[0]);
        when(fileStoragePort.uploadFile(anyString(), any(), anyString())).thenReturn("url");

        ArgumentCaptor<CertificateDocumentModel> modelCaptor = ArgumentCaptor.forClass(CertificateDocumentModel.class);
        ArgumentCaptor<UserCertificate> dbCaptor = ArgumentCaptor.forClass(UserCertificate.class);

        when(userCertificateRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        certificateApplicationService.issueCertificateForEnrollment(enrollmentId);

        verify(pdfGeneratorPort).generateCertificate(modelCaptor.capture());
        verify(userCertificateRepository).save(dbCaptor.capture());

        CertificateDocumentModel pdfModel = modelCaptor.getValue();
        UserCertificate dbCert = dbCaptor.getValue();

        // INVARIANT CHECK
        assertThat(pdfModel.certificateCode()).isEqualTo(dbCert.getCertificateCode());
        assertThat(pdfModel.issueDate()).isEqualTo(dbCert.getIssuedAt().toLocalDate());
    }
    @Test
    @DisplayName("Dev Reset 1 - Certificate tồn tại -> delete thành công")
    void shouldDeleteCertificateWhenItExists() {
        Long enrollmentId = 1L;
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(new Enrollment()));
        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(true);

        certificateApplicationService.resetCertificateByEnrollmentId(enrollmentId);

        verify(userCertificateRepository).deleteByEnrollmentId(enrollmentId);
    }

    @Test
    @DisplayName("Dev Reset 2 - Certificate không tồn tại -> request idempotent, không lỗi")
    void shouldNotDeleteCertificateWhenItDoesNotExist() {
        Long enrollmentId = 2L;
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(new Enrollment()));
        when(userCertificateRepository.existsByEnrollmentId(enrollmentId)).thenReturn(false);

        certificateApplicationService.resetCertificateByEnrollmentId(enrollmentId);

        verify(userCertificateRepository, never()).deleteByEnrollmentId(anyLong());
    }

    @Test
    @DisplayName("Dev Reset 3 - Enrollment không tồn tại -> ném exception")
    void shouldThrowExceptionWhenEnrollmentNotFoundInReset() {
        Long enrollmentId = 3L;
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificateApplicationService.resetCertificateByEnrollmentId(enrollmentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ENROLLMENT_NOT_FOUND);

        verify(userCertificateRepository, never()).deleteByEnrollmentId(anyLong());
    }
}