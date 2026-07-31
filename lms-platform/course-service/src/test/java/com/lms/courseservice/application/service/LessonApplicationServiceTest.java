package com.lms.courseservice.application.service;

import com.lms.courseservice.adapter.in.rest.dto.LessonResourceRequest;
import com.lms.courseservice.adapter.in.rest.dto.LessonResponse;
import com.lms.courseservice.adapter.in.rest.dto.LessonUpsertRequest;
import com.lms.courseservice.adapter.out.persistence.mapper.LessonMapper;
import com.lms.courseservice.application.port.out.LessonRepositoryPort;
import com.lms.courseservice.application.port.out.ModuleRepositoryPort;
import com.lms.security.util.SecurityUtils;
import com.lms.courseservice.domain.enums.LessonType;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Lesson;
import com.lms.courseservice.domain.model.Module;
import com.lms.shared.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonApplicationServiceTest {

    @Mock
    private LessonRepositoryPort lessonRepository;

    @Mock
    private ModuleRepositoryPort moduleRepository;

    @Mock
    private LessonMapper lessonMapper;

    @InjectMocks
    private LessonApplicationService lessonApplicationService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    private Module mockModule;
    private Course mockCourse;

    @BeforeEach
    void setUp() {
        // 1. Giả lập SecurityUtils (Static Mocking) để bỏ qua checkOwnership trong Unit Test
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
        mockedSecurityUtils.when(() -> SecurityUtils.checkOwnership(anyString())).thenAnswer(invocation -> null);

        // 2. Tạo Mock Data dùng chung
        mockCourse = Course.builder().instructor("instructor-123").build();
        mockModule = Module.builder().course(mockCourse).title("Module 1").build();
    }

    @AfterEach
    void tearDown() {
        // Bắt buộc đóng MockStatic sau mỗi test để không ảnh hưởng test case khác
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("Thêm Bài giảng thành công cùng với Tài liệu đính kèm (Rich Domain)")
    void should_AddLesson_Successfully() {
        // Arrange (Chuẩn bị dữ liệu)
        Long moduleId = 1L;
        LessonResourceRequest resourceReq = LessonResourceRequest.builder()
                .title("Slide PDF").fileUrl("https://url.com").fileType("PDF").fileSizeBytes(1024L).build();

        LessonUpsertRequest request = LessonUpsertRequest.builder()
                .title("Test Lesson")
                .lessonType(LessonType.VIDEO)
                .resources(List.of(resourceReq))
                .build();

        LessonResponse expectedResponse = LessonResponse.builder().title("Test Lesson").build();

        // Giả lập hành vi của Repository & Mapper
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(mockModule));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lessonMapper.toResponse(any(Lesson.class))).thenReturn(expectedResponse);

        // Act (Thực thi hàm cần test)
        LessonResponse actualResponse = lessonApplicationService.addLesson(moduleId, request);

        // Assert (Kiểm chứng kết quả)
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getTitle()).isEqualTo("Test Lesson");

        // Xác minh xem Repository có được gọi đúng 1 lần với object Lesson đã có đủ Resource không
        verify(lessonRepository, times(1)).save(argThat(lesson ->
                lesson.getTitle().equals("Test Lesson") &&
                        lesson.getResources().size() == 1 &&
                        lesson.getResources().getFirst().getTitle().equals("Slide PDF")
        ));
    }

    @Test
    @DisplayName("Báo lỗi BusinessException khi thêm Bài giảng vào Module không tồn tại")
    void should_ThrowException_When_AddLessonToUnknownModule() {
        // Arrange
        Long invalidModuleId = 99L;
        LessonUpsertRequest request = LessonUpsertRequest.builder().title("Test").build();

        when(moduleRepository.findById(invalidModuleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> lessonApplicationService.addLesson(invalidModuleId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Module not found");

        // Xác minh Repository tuyệt đối KHÔNG được gọi lệnh lưu
        verify(lessonRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cập nhật Bài giảng thành công và thay thế Tài liệu đính kèm")
    void should_UpdateLesson_Successfully_And_ReplaceResources() {
        // Arrange
        Long lessonId = 1L;
        Lesson existingLesson = Lesson.create(mockModule, "Old Title", null, null, 0, LessonType.TEXT, false, 1);
        existingLesson.addResource("Old Doc", "url", "PDF", 100L); // Có 1 resource cũ

        LessonResourceRequest newResourceReq = LessonResourceRequest.builder()
                .title("New Video").fileUrl("new-url").fileType("MP4").fileSizeBytes(200L).build();

        LessonUpsertRequest request = LessonUpsertRequest.builder()
                .title("New Title")
                .resources(List.of(newResourceReq))
                .build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(existingLesson));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        lessonApplicationService.updateLesson(lessonId, request);

        // Assert
        verify(lessonRepository, times(1)).save(argThat(lesson ->
                lesson.getTitle().equals("New Title") &&
                        lesson.getResources().size() == 1 && // Resource cũ phải bị xóa (clearResources)
                        lesson.getResources().getFirst().getTitle().equals("New Video") // Chỉ còn Resource mới
        ));
    }
}