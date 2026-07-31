package com.lms.courseservice.application.service;

import com.lms.courseservice.adapter.in.rest.dto.ModuleResponse;
import com.lms.courseservice.adapter.in.rest.dto.ModuleUpsertRequest;
import com.lms.courseservice.adapter.out.persistence.mapper.ModuleMapper;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.courseservice.application.port.out.ModuleRepositoryPort;
import com.lms.security.util.SecurityUtils;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Module;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleApplicationServiceTest {

    @Mock
    private ModuleRepositoryPort moduleRepository;

    @Mock
    private CourseRepositoryPort courseRepository;

    @Mock
    private ModuleMapper moduleMapper;

    private ModuleApplicationService moduleApplicationService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private Course mockCourse;

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
        // Mặc định cho phép mọi checkOwnership đi qua thành công (do nothing)
        mockedSecurityUtils.when(() -> SecurityUtils.checkOwnership(anyString())).thenAnswer(inv -> null);

        mockCourse = Course.builder().instructor("valid-instructor-id").build();
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("Thêm Module thành công - Đã vượt qua bước kiểm tra quyền")
    void should_AddModule_Successfully_When_IsOwner() {
        Long courseId = 1L;
        ModuleUpsertRequest request = ModuleUpsertRequest.builder().title("Microservices 101").sortOrder(1).build();
        ModuleResponse expectedResponse = ModuleResponse.builder().title("Microservices 101").build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));
        when(moduleRepository.save(any(Module.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleMapper.toResponse(any(Module.class))).thenReturn(expectedResponse);

        ModuleResponse actualResponse = moduleApplicationService.addModule(courseId, request);

        assertThat(actualResponse).isNotNull();
        // Kiểm chứng xem phương thức SecurityUtils.checkOwnership có thực sự được gọi để bảo mật hệ thống chưa
        mockedSecurityUtils.verify(() -> SecurityUtils.checkOwnership("valid-instructor-id"), times(1));

        verify(moduleRepository, times(1)).save(argThat(module ->
                module.getTitle().equals("Microservices 101") &&
                        module.getCourse() != null
        ));
    }

    @Test
    @DisplayName("Sửa Module thành công - Kiểm tra cập nhật details")
    void should_UpdateModule_Successfully() {
        Long moduleId = 1L;
        Module existingModule = Module.create(mockCourse, "Old Title", 0);
        ModuleUpsertRequest request = ModuleUpsertRequest.builder().title("New Title").sortOrder(5).build();

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(existingModule));
        when(moduleRepository.save(any(Module.class))).thenAnswer(inv -> inv.getArgument(0));

        moduleApplicationService.updateModule(moduleId, request);

        // Đảm bảo module đã kích hoạt hành vi updateDetails của Rich Domain Model
        verify(moduleRepository, times(1)).save(argThat(module ->
                module.getTitle().equals("New Title") &&
                        module.getSortOrder() == 5
        ));
    }
}