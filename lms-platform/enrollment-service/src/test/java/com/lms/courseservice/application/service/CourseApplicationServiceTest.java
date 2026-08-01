package com.lms.courseservice.application.service;

import com.lms.courseservice.adapter.in.rest.dto.CourseResponse;
import com.lms.courseservice.adapter.in.rest.dto.CourseUpsertRequest;
import com.lms.courseservice.adapter.out.persistence.mapper.CourseMapper;
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

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseApplicationService courseApplicationService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    private final String CURRENT_USER_ID = "instructor-uuid-123";
    private Category mockCategory; // Thêm mock category

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);

        // Chuẩn bị sẵn một danh mục giả để vượt qua Business Rule
        mockCategory = Category.builder().build();
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("Tạo khóa học thành công - Lấy đúng Instructor ID từ JWT")
    void should_CreateCourse_Successfully() {
        // Giả lập JWT Auth
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);

        // Đã FIX: Truyền thêm categoryId để không bị dính lỗi BusinessException
        CourseUpsertRequest request = CourseUpsertRequest.builder()
                .title("Spring Boot Mastery")
                .price(BigDecimal.valueOf(1000))
                .categoryId(99L)
                .build();

        CourseResponse expectedResponse = CourseResponse.builder().title("Spring Boot Mastery").instructorId(CURRENT_USER_ID).build();

        // MOCK THÊM: Trả về danh mục giả khi hệ thống tìm kiếm ID = 99L
        when(categoryRepository.findById(99L)).thenReturn(Optional.of(mockCategory));
        when(courseRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));
        when(courseMapper.toResponse(any(Course.class))).thenReturn(expectedResponse);

        CourseResponse actualResponse = courseApplicationService.createCourse(request);

        assertThat(actualResponse).isNotNull();
        verify(courseRepository, times(1)).save(argThat(course ->
                course.getInstructor().equals(CURRENT_USER_ID) &&
                        course.getStatus() == CourseStatus.DRAFT
        ));
    }

    @Test
    @DisplayName("Cập nhật thất bại - Báo lỗi Access Denied nếu không phải chủ sở hữu")
    void should_ThrowException_When_UpdateCourse_NotOwner() {
        Long courseId = 1L;
        Course existingCourse = Course.builder().title("Old").instructor("different-user-456").build();
        CourseUpsertRequest request = CourseUpsertRequest.builder().title("New").build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));

        // FIX: Đảm bảo thông báo lỗi quăng ra trùng khớp 100% với những gì ta đang test
        mockedSecurityUtils.when(() -> SecurityUtils.checkOwnership("different-user-456"))
                .thenThrow(new BusinessException(ErrorCode.ACCESS_DENIED, "Access denied."));

        assertThatThrownBy(() -> courseApplicationService.updateCourse(courseId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Access denied."); // Bắt chính xác message này

        verify(courseRepository, never()).save(any());
    }
}