package com.lms.courseservice.application.service;

import com.lms.courseservice.application.port.in.command.LessonCommand;
import com.lms.courseservice.application.port.out.CourseProjectionPort;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.courseservice.domain.enums.LessonType;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Lesson;
import com.lms.courseservice.domain.model.Module;
import com.lms.security.util.SecurityUtils;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonApplicationServiceTest {

    @Mock
    private CourseRepositoryPort courseRepository;

    @Mock
    private CourseProjectionPort projectionPort;

    @InjectMocks
    private LessonApplicationService lessonApplicationService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private Course mockCourse;
    private Module mockModule;

    @BeforeEach
    void setUp() {
        // Mock SecurityUtils để bypass việc check quyền sở hữu
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
        mockedSecurityUtils.when(() -> SecurityUtils.checkOwnership(any())).thenAnswer(inv -> null);

        // Chuẩn bị sẵn 1 Aggregate (Course chứa 1 Module)
        mockCourse = Course.builder().instructor("instructor-uuid-123").modules(new ArrayList<>()).build();
        ReflectionTestUtils.setField(mockCourse, "id", 10L);

        mockModule = Module.create(mockCourse, "Module 1", 0);
        ReflectionTestUtils.setField(mockModule, "id", 20L);
        mockCourse.getModules().add(mockModule);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("BUG-01 Regression: POST Lesson must return Lesson with DB-generated ID")
    void shouldReturnLessonWithId_WhenAddLesson() {
        // Arrange
        Long moduleId = 20L;
        Long courseId = 10L;
        Long expectedGeneratedLessonId = 35L;

        LessonCommand command = new LessonCommand(
                "Lesson 1", "Content", "url", 600,
                LessonType.VIDEO, true, 0, null
        );

        when(courseRepository.findModuleById(moduleId)).thenReturn(Optional.of(mockModule));
        when(courseRepository.findByIdWithFullCurriculum(courseId)).thenReturn(Optional.of(mockCourse));

        // Mock hành vi sinh ID của Database/Hibernate khi gọi repository.save()
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
            Course savedAgg = inv.getArgument(0);
            Lesson lessonToSave = savedAgg.getModules().get(0).getLessons().get(0);
            
            // Ép ID vào đối tượng giống như cách Hibernate sinh khóa chính
            ReflectionTestUtils.setField(lessonToSave, "id", expectedGeneratedLessonId);
            return savedAgg;
        });

        // Act
        Lesson result = lessonApplicationService.addLesson(moduleId, command);

        // Assert
        assertNotNull(result.getId(), "BUG-01 LẶP LẠI: ID bài học bị null");
        assertEquals(expectedGeneratedLessonId, result.getId(), "ID bài học không khớp với DB generated ID");
        assertEquals(command.title(), result.getTitle());
        
        // Verify Kafka event triggered
        verify(projectionPort, times(1)).publish(any());
    }
}