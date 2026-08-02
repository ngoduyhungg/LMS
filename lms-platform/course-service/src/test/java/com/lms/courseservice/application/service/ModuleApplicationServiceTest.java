package com.lms.courseservice.application.service;

import com.lms.courseservice.application.port.in.command.ModuleCommand;
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
                        course.getModules().get(0).getTitle().equals("Microservices 101")
        ));
    }

    @Test
    @DisplayName("Sửa Module thành công thông qua Aggregate Course")
    void should_UpdateModule_Successfully() {
        Long moduleId = 1L;
        Module existingModule = Module.create(mockCourse, "Old Title", 0);
        mockCourse.getModules().add(existingModule);

        // Dùng ModuleCommand thay cho DTO
        ModuleCommand command = new ModuleCommand("New Title", 5);

        when(courseRepository.findModuleById(moduleId)).thenReturn(Optional.of(existingModule));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        moduleApplicationService.updateModule(moduleId, command);

        verify(courseRepository, times(1)).save(argThat(course ->
                course.getModules().get(0).getTitle().equals("New Title") &&
                        course.getModules().get(0).getSortOrder() == 5
        ));
    }
}