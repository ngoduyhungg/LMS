package com.lms.courseservice.application.service;

import com.lms.courseservice.adapter.in.rest.dto.ModuleUpsertRequest;
import com.lms.courseservice.application.port.in.GetModuleUseCase;
import com.lms.courseservice.application.port.in.ManageModuleUseCase;
import com.lms.courseservice.application.port.in.command.ModuleCommand;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.security.util.SecurityUtils;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Module;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleApplicationService implements ManageModuleUseCase, GetModuleUseCase {

    private final CourseRepositoryPort courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Module> getModulesByCourseId(Long courseId){
        Course course = courseRepository.findByIdWithFullCurriculum(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Course not found this ID: " + courseId));
        return course.getModules();
    }

    @Override
    @Transactional(readOnly = true)
    public Module getModuleById(Long moduleId){
        return courseRepository.findModuleById(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND, "Module not found with ID: " + moduleId));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public Module addModule(Long courseId, ModuleCommand request) {
        Course course = courseRepository.findByIdWithFullCurriculum(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Course not found with ID: " + courseId));

        SecurityUtils.checkOwnership(course.getInstructor());

        Module newModule = Module.create(course, request.title(), request.sortOrder());
        course.getModules().add(newModule);

        Course savedCourse = courseRepository.save(course);

        return savedCourse.getModules().stream()
                .reduce((first, second) -> second)
                .orElse(newModule);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public Module updateModule(Long moduleId, ModuleCommand request) {
        Module module = courseRepository.findModuleById(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND, "Module not found with id: " + moduleId));

        Course course = module.getCourse();
        SecurityUtils.checkOwnership(course.getInstructor());

        module.updateDetails(request.title(), request.sortOrder());
        courseRepository.save(course); // Cascade update

        return module;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void deleteModule(Long moduleId) {
        Module module = courseRepository.findModuleById(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND, "Module not found with id: " + moduleId));

        Course course = module.getCourse();
        SecurityUtils.checkOwnership(course.getInstructor());

        course.getModules().removeIf(m -> m.getId().equals(moduleId));
        courseRepository.save(course); // Cascade delete
    }
}