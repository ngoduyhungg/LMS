package com.lms.courseservice.application.service;

import com.lms.courseservice.application.port.in.GetLessonUseCase;
import com.lms.courseservice.application.port.in.ManageLessonUseCase;
import com.lms.courseservice.application.port.in.command.LessonCommand;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.security.util.SecurityUtils;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Lesson;
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
public class LessonApplicationService implements ManageLessonUseCase, GetLessonUseCase {

    private final CourseRepositoryPort courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Lesson> getLessonsByModuleId(Long moduleId){
        Module module = courseRepository.findModuleById(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND,"Module not found with ID: " + moduleId));
        return module.getLessons();
    }

    @Override
    @Transactional(readOnly = true)
    public Lesson getLessonById(Long lessonId){
        return courseRepository.findLessonById(lessonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LESSON_NOT_FOUND,"Lesson not found with ID: " + lessonId));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public Lesson addLesson(Long moduleId, LessonCommand request) {
        Module module = courseRepository.findModuleById(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND,"Module not found with id: " + moduleId));

        Course course = module.getCourse();
        SecurityUtils.checkOwnership(course.getInstructor());

        Lesson newLesson = Lesson.create(
                module, request.title(), request.content(), request.videoUrl(),
                request.durationSeconds(), request.lessonType(),
                request.isPreview(), request.sortOrder());

        if(request.resources() != null){
            request.resources().forEach(res ->
                    newLesson.addResource(res.title(), res.fileUrl(), res.fileType(), res.fileSizeBytes())
            );
        }

        module.getLessons().add(newLesson);
        courseRepository.save(course); // Lưu Aggregate root sẽ tự lưu cascade

        return newLesson;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public Lesson updateLesson(Long lessonId, LessonCommand request) {
        Lesson lesson = courseRepository.findLessonById(lessonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LESSON_NOT_FOUND,"Lesson not found with id: " + lessonId));

        Course course = lesson.getModule().getCourse();
        SecurityUtils.checkOwnership(course.getInstructor());

        lesson.updateDetails(
                request.title(), request.content(), request.videoUrl(),
                request.durationSeconds(), request.lessonType(),
                request.isPreview(), request.sortOrder()
        );

        lesson.clearResources();
        if(request.resources() != null){
            request.resources().forEach(res ->
                    lesson.addResource(res.title(), res.fileUrl(), res.fileType(), res.fileSizeBytes())
            );
        }

        courseRepository.save(course);
        return lesson;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void deleteLesson(Long lessonId) {
        Lesson lesson = courseRepository.findLessonById(lessonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LESSON_NOT_FOUND,"Lesson not found with id: " + lessonId));

        Course course = lesson.getModule().getCourse();
        SecurityUtils.checkOwnership(course.getInstructor());

        lesson.getModule().getLessons().removeIf(l -> l.getId().equals(lessonId));
        courseRepository.save(course);
    }
}