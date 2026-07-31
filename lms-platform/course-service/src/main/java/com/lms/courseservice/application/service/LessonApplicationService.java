package com.lms.courseservice.application.service;

import com.lms.courseservice.adapter.in.rest.dto.LessonResponse;
import com.lms.courseservice.adapter.in.rest.dto.LessonUpsertRequest;
import com.lms.courseservice.adapter.out.persistence.mapper.LessonMapper;
import com.lms.courseservice.application.port.in.GetLessonUseCase;
import com.lms.courseservice.application.port.in.ManageLessonUseCase;
import com.lms.courseservice.application.port.out.LessonRepositoryPort;
import com.lms.courseservice.application.port.out.ModuleRepositoryPort;
import com.lms.security.util.SecurityUtils;
import com.lms.courseservice.domain.model.Lesson;
import com.lms.courseservice.domain.model.Module;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application Service quản lý Bài học (Lesson) và Tài liệu đính kèm (LessonResource).
 * Tuân thủ SRP: Chỉ chứa nghiệp vụ liên quan đến Lesson.
 */
@Service
@RequiredArgsConstructor
public class LessonApplicationService implements ManageLessonUseCase, GetLessonUseCase {

    private final LessonRepositoryPort lessonRepository;
    private final ModuleRepositoryPort moduleRepository;
    private final LessonMapper lessonMapper;

    // ===== READ USE CASES =====
    public List<LessonResponse> getLessonsByModuleId(Long moduleId){
        if(moduleRepository.findById(moduleId).isEmpty()){
            throw new BusinessException(ErrorCode.MODULE_NOT_FOUND,"Module not found with ID: " + moduleId);
        }
        List<Lesson> lessons = lessonRepository.findAllByModuleIdOrderBySortOrder(moduleId);
        return lessonMapper.toResponseList(lessons);
    }
    public LessonResponse getLessonById(Long lessonId){
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new BusinessException(ErrorCode.LESSON_NOT_FOUND,"Lesson not found with ID: " + lessonId));
        return lessonMapper.toResponse(lesson);
    }

    // ===== COMMAND USE CASES (WRITE) =====
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public LessonResponse addLesson(Long moduleId, LessonUpsertRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND,"Module not found with id: " + moduleId));

        SecurityUtils.checkOwnership(module.getCourse().getInstructor());

        Lesson lesson = Lesson.create(
                module ,request.getTitle(), request.getContent(), request.getVideoUrl(),
                request.getDurationSeconds(), request.getLessonType(),
                request.getIsPreview(), request.getSortOrder());

        if(request.getResources() != null){
            request.getResources().forEach(res ->
                    lesson.addResource(res.getTitle(), res.getFileUrl(), res.getFileType(), res.getFileSizeBytes())
            );
        }
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public LessonResponse updateLesson(Long lessonId, LessonUpsertRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LESSON_NOT_FOUND,"Lesson not found with id: " + lessonId));

        SecurityUtils.checkOwnership(lesson.getModule().getCourse().getInstructor());
        lesson.updateDetails(
                request.getTitle(), request.getContent(), request.getVideoUrl(),
                request.getDurationSeconds(), request.getLessonType(),
                request.getIsPreview(), request.getSortOrder()
        );
        lesson.clearResources();
        if(request.getResources() != null){
            request.getResources().forEach(res ->
                    lesson.addResource(res.getTitle(), res.getFileUrl(), res.getFileType(), res.getFileSizeBytes())
            );
        }
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LESSON_NOT_FOUND,"Lesson not found with id: " + lessonId));

        SecurityUtils.checkOwnership(lesson.getModule().getCourse().getInstructor());

        lessonRepository.deleteById(lessonId);
    }
}