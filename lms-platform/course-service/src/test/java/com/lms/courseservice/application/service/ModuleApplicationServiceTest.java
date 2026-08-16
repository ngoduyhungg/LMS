package com.lms.courseservice.application.service;

import com.lms.courseservice.application.port.in.command.ModuleCommand;
import com.lms.courseservice.application.port.out.CourseProjectionPort;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.security.util.SecurityUtils;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Module;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleApplicationServiceTest {

    @Mock
    private CourseRepositoryPort courseRepository;

    @InjectMocks
    private ModuleApplicationService moduleApplicationService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private Course mockCourse;
    @Mock private CourseProjectionPort courseProjectionPort;

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
        mockedSecurityUtils.when(() -> SecurityUtils.checkOwnership(anyString())).thenAnswer(inv -> null);

        mockCourse = Course.builder().instructor("valid-instructor-id").modules(new ArrayList<>()).build();
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("Thêm Module thành công thông qua Aggregate Course")
    void should_AddModule_Successfully_When_IsOwner() {
        Long courseId = 1L;

        // Dùng ModuleCommand thay cho DTO
        ModuleCommand command = new ModuleCommand("Microservices 101", 1);

        when(courseRepository.findByIdWithFullCurriculum(courseId)).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        Module actualModule = moduleApplicationService.addModule(courseId, command);

        assertThat(actualModule).isNotNull();
        assertThat(actualModule.getTitle()).isEqualTo("Microservices 101");

        mockedSecurityUtils.verify(() -> SecurityUtils.checkOwnership("valid-instructor-id"), times(1));

        verify(courseRepository, times(1)).save(argThat(course ->
                course.getModules().size() == 1 &&
                        course.getModules().getFirst().getTitle().equals("Microservices 101")
        ));
    }

    @Test
    @DisplayName("Sửa Module thành công thông qua Aggregate Course")
    void should_UpdateModule_Successfully() {
        Long moduleId = 1L;
        Long courseId = 99L; // Cấp 1 ID giả cho Course

        // 1. Gán ID cho Course để tránh null
        ReflectionTestUtils.setField(mockCourse, "id", courseId);

        // 2. Tạo Module và gán ID cho Module để đoạn stream().filter() không bị lỗi
        Module existingModule = Module.create(mockCourse, "Old Title", 0);
        ReflectionTestUtils.setField(existingModule, "id", moduleId);

        mockCourse.getModules().add(existingModule);

        // Dùng ModuleCommand thay cho DTO
        ModuleCommand command = new ModuleCommand("New Title", 5);

        // 3. Mock tìm partial module
        when(courseRepository.findModuleById(moduleId)).thenReturn(Optional.of(existingModule));

        // 4. THÊM DÒNG NÀY ĐỂ FIX LỖI: Mock tìm Full Course
        when(courseRepository.findByIdWithFullCurriculum(courseId)).thenReturn(Optional.of(mockCourse));

        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        // 5. Thực thi
        moduleApplicationService.updateModule(moduleId, command);

        // 6. Kiểm tra
        verify(courseRepository, times(1)).save(argThat(course ->
                course.getModules().get(0).getTitle().equals("New Title") &&
                        course.getModules().get(0).getSortOrder() == 5
        ));
    }
}