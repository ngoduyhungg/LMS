package com.lms.courseservice.application.service;

import com.lms.courseservice.application.port.in.GetLessonUseCase;
import com.lms.courseservice.application.port.in.ManageLessonUseCase;
import com.lms.courseservice.application.port.in.command.LessonCommand;
import com.lms.courseservice.application.port.out.CourseProjectionPort;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.courseservice.application.port.out.dto.CourseProjectionPayload;
import com.lms.security.util.SecurityUtils;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Lesson;
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
public class LessonApplicationService implements ManageLessonUseCase, GetLessonUseCase {

    private final CourseRepositoryPort courseRepository;
    private final CourseProjectionPort projectionPort;

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
        // 1. Mượn findModuleById để lấy courseId
        Module partialModule = courseRepository.findModuleById(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND,"Module not found with id: " + moduleId));
        Long courseId = partialModule.getCourse().getId();

        // 2. Load FULL Course Aggregate Root
        Course fullCourse = courseRepository.findByIdWithFullCurriculum(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Course not found"));

        SecurityUtils.checkOwnership(fullCourse.getInstructor());

        // 3. Tìm đúng module bên trong FULL Course
        Module targetModule = fullCourse.getModules().stream()
                .filter(m -> m.getId().equals(moduleId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND, "Module not found in aggregate"));

        // 4. Tạo và gán Lesson
        Lesson newLesson = Lesson.create(
                targetModule, request.title(), request.content(), request.videoUrl(),
                request.durationSeconds(), request.lessonType(),
                request.isPreview(), request.sortOrder());

        if(request.resources() != null){
            request.resources().forEach(res ->
                    newLesson.addResource(res.title(), res.fileUrl(), res.fileType(), res.fileSizeBytes())
            );
        }

        targetModule.getLessons().add(newLesson);

        // 5. Save FULL Course và Publish
        courseRepository.save(fullCourse);
        publishCourseProjection(fullCourse);

        return newLesson;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public Lesson updateLesson(Long lessonId, LessonCommand request) {
        // FIX TƯƠNG TỰ CHO UPDATE
        Lesson partialLesson = courseRepository.findLessonById(lessonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LESSON_NOT_FOUND,"Lesson not found with id: " + lessonId));
        Long courseId = partialLesson.getModule().getCourse().getId();

        Course fullCourse = courseRepository.findByIdWithFullCurriculum(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Course not found"));

        SecurityUtils.checkOwnership(fullCourse.getInstructor());

        // Dò tìm Lesson đúng bên trong fullCourse
        Lesson targetLesson = fullCourse.getModules().stream()
                .flatMap(m -> m.getLessons().stream())
                .filter(l -> l.getId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.LESSON_NOT_FOUND, "Lesson not found in aggregate"));

        targetLesson.updateDetails(
                request.title(), request.content(), request.videoUrl(),
                request.durationSeconds(), request.lessonType(),
                request.isPreview(), request.sortOrder()
        );

        targetLesson.clearResources();
        if(request.resources() != null){
            request.resources().forEach(res ->
                    targetLesson.addResource(res.title(), res.fileUrl(), res.fileType(), res.fileSizeBytes())
            );
        }

        courseRepository.save(fullCourse);
        return targetLesson;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void deleteLesson(Long lessonId) {
        // FIX TƯƠNG TỰ CHO DELETE
        Lesson partialLesson = courseRepository.findLessonById(lessonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LESSON_NOT_FOUND,"Lesson not found with id: " + lessonId));
        Long courseId = partialLesson.getModule().getCourse().getId();

        Course fullCourse = courseRepository.findByIdWithFullCurriculum(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Course not found"));

        SecurityUtils.checkOwnership(fullCourse.getInstructor());

        // Quét tất cả modules và xóa lesson khớp ID
        fullCourse.getModules().forEach(m -> m.getLessons().removeIf(l -> l.getId().equals(lessonId)));

        courseRepository.save(fullCourse);
        publishCourseProjection(fullCourse);
    }

    private void publishCourseProjection(Course course) {
        long totalLessons = courseRepository.countLessonsByCourseId(course.getId());
        log.info("Publishing CourseProjection - courseId: {}, instructorId: {}, totalLessons: {}",
                course.getId(), course.getInstructor(), totalLessons);
        projectionPort.publish(new CourseProjectionPayload(course.getId(), course.getInstructor(), totalLessons));
    }
}