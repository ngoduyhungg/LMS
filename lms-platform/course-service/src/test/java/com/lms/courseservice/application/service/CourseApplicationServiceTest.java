package com.lms.courseservice.application.service;

import com.lms.courseservice.application.port.in.command.CourseCommand;
import com.lms.courseservice.application.port.out.CategoryRepositoryPort;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.security.util.SecurityUtils;
import com.lms.courseservice.domain.enums.CourseStatus;
import com.lms.courseservice.domain.model.Category;
import com.lms.courseservice.domain.model.Course;
import com.lms.shared.enums.ErrorCode;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseApplicationServiceTest {

    @Mock
    private CourseRepositoryPort courseRepository;

    @Mock
    private CategoryRepositoryPort categoryRepository;

    @InjectMocks
    private CourseApplicationService courseApplicationService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    private final String CURRENT_USER_ID = "instructor-uuid-123";
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
        mockCategory = Category.builder().build();
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("Tạo khóa học thành công - Lấy đúng Instructor ID từ JWT")
    void should_CreateCourse_Successfully() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);

        // Khởi tạo CourseCommand bằng constructor của Record
        CourseCommand command = new CourseCommand(
                "Spring Boot Mastery",
                null,
                null,
                BigDecimal.valueOf(1000),
                null,
                99L,
                null,
                null
        );

        when(categoryRepository.findById(99L)).thenReturn(Optional.of(mockCategory));
        when(courseRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        Course actualCourse = courseApplicationService.createCourse(command);

        assertThat(actualCourse).isNotNull();
        assertThat(actualCourse.getInstructor()).isEqualTo(CURRENT_USER_ID);
        assertThat(actualCourse.getStatus()).isEqualTo(CourseStatus.DRAFT);

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Cập nhật thất bại - Báo lỗi Access Denied nếu không phải chủ sở hữu")
    void should_ThrowException_When_UpdateCourse_NotOwner() {
        Long courseId = 1L;
        Course existingCourse = Course.builder().title("Old").instructor("different-user-456").build();

        // Khởi tạo CourseCommand
        CourseCommand command = new CourseCommand("New", null, null, null, null, null, null, null);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));

        mockedSecurityUtils.when(() -> SecurityUtils.checkOwnership("different-user-456"))
                .thenThrow(new BusinessException(ErrorCode.ACCESS_DENIED, "Access denied."));

        assertThatThrownBy(() -> courseApplicationService.updateCourse(courseId, command))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bizEx = (BusinessException) ex;
                    assertThat(bizEx.getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
                });

        verify(courseRepository, never()).save(any());
    }
}