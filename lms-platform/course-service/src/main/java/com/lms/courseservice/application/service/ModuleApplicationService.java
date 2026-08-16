package com.lms.courseservice.application.service;

import com.lms.courseservice.application.port.in.GetModuleUseCase;
import com.lms.courseservice.application.port.in.ManageModuleUseCase;
import com.lms.courseservice.application.port.in.command.ModuleCommand;
import com.lms.courseservice.application.port.out.CourseProjectionPort;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.courseservice.application.port.out.dto.CourseProjectionPayload;
import com.lms.security.util.SecurityUtils;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Module;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleApplicationService implements ManageModuleUseCase, GetModuleUseCase {

    private final CourseRepositoryPort courseRepository;
    private final CourseProjectionPort projectionPort;

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
        Module partialModule = courseRepository.findModuleById(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND, "Module not found with id: " + moduleId));
        Long courseId = partialModule.getCourse().getId();

        Course fullCourse = courseRepository.findByIdWithFullCurriculum(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Course not found"));

        SecurityUtils.checkOwnership(fullCourse.getInstructor());

        Module targetModule = fullCourse.getModules().stream()
                .filter(m -> m.getId().equals(moduleId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND, "Module not found in aggregate"));

        targetModule.updateDetails(request.title(), request.sortOrder());
        courseRepository.save(fullCourse); // Cascade update an toàn

        return targetModule;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void deleteModule(Long moduleId) {
        Module partialModule = courseRepository.findModuleById(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND, "Module not found with id: " + moduleId));
        Long courseId = partialModule.getCourse().getId();

        Course fullCourse = courseRepository.findByIdWithFullCurriculum(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Course not found"));

        SecurityUtils.checkOwnership(fullCourse.getInstructor());

        fullCourse.getModules().removeIf(m -> m.getId().equals(moduleId));

        courseRepository.save(fullCourse);

        long totalLessons = courseRepository.countLessonsByCourseId(fullCourse.getId());
        log.info("Publishing CourseProjection - courseId: {}, instructorId: {}, totalLessons: {}",
                fullCourse.getId(), fullCourse.getInstructor(), totalLessons);
        projectionPort.publish(new CourseProjectionPayload(fullCourse.getId(), fullCourse.getInstructor(), totalLessons));
    }
}