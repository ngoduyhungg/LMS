package com.lms.courseservice.application.service;

import com.lms.courseservice.application.port.in.ManageCourseUseCase;
import com.lms.courseservice.application.port.in.GetCourseUseCase;
import com.lms.courseservice.application.port.in.command.CourseCommand;
import com.lms.courseservice.application.port.out.CategoryRepositoryPort;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.security.util.SecurityUtils;
import com.lms.courseservice.domain.enums.CourseStatus;
import com.lms.courseservice.domain.model.Category;
import com.lms.courseservice.domain.model.Course;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseApplicationService implements GetCourseUseCase, ManageCourseUseCase {

    private final CourseRepositoryPort courseRepository;
    private final CategoryRepositoryPort categoryRepository;

    // =========================================================
    // SLUG GENERATION
    // =========================================================
    private String normalizeToAsciiSlug(String title) {
        if (title == null || title.isBlank()) return "course";
        String processed = title.replace("đ", "d").replace("Đ", "D");
        String normalized = Normalizer.normalize(processed, Normalizer.Form.NFD);
        String ascii = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return ascii.toLowerCase().trim().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-").replaceAll("-+", "-").replaceAll("^-|-$", "");
    }

    private String generateSlug(String title) {
        String base = normalizeToAsciiSlug(title);
        if (base.isBlank()) base = "course";
        if (courseRepository.findBySlug(base).isEmpty()) return base;
        String candidate;
        do {
            String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 4);
            candidate = base + "-" + suffix;
        } while (courseRepository.findBySlug(candidate).isPresent());
        return candidate;
    }

    // =========================================================
    // GET USE CASES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Course> getAllPublishedCourses() {
        return courseRepository.findAllByStatus(CourseStatus.PUBLISHED);
    }

    @Override
    @Transactional(readOnly = true)
    public Course getCourseDetail(String slug) {
        return courseRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Course not found with slug: " + slug));
    }

    @Override
    @Transactional(readOnly = true)
    public Course getCurriculum(Long courseId) {
        // load eager modules/lessons
        return courseRepository.findByIdWithFullCurriculum(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Course not found with id: " + courseId));
    }

    // =========================================================
    // MANAGE COURSE USE CASES
    // =========================================================

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public Course createCourse(CourseCommand request) {
        String instructorId = SecurityUtils.getCurrentUserId();

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, "Category not found with id: " + request.categoryId()));
        }

        String slug = generateSlug(request.title());
        Course course = Course.create(
                request.title(),
                slug,
                request.summary(),
                request.description(),
                request.price(),
                request.level(),
                request.thumbnailUrl(),
                category,
                instructorId
        );
        return courseRepository.save(course);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public Course updateCourse(Long id, CourseCommand request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Course not found with id: " + id));

        SecurityUtils.checkOwnership(course.getInstructor());

        course.updateCourseInfo(
                request.title(),
                request.summary(),
                request.description(),
                request.price(),
                request.level(),
                request.thumbnailUrl()
        );

        if(request.categoryId() != null){
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, "Category not found with ID: " + request.categoryId()));
            course.assignCategory(category);
        }

        if(request.status() == CourseStatus.PUBLISHED){
            course.publish();
        } else if(request.status() == CourseStatus.ARCHIVED){
            course.archive();
        }

        return courseRepository.save(course);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Course not found with id: " + id));
        SecurityUtils.checkOwnership(course.getInstructor());
        courseRepository.deleteById(id);
    }
}