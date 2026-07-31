package com.lms.courseservice.application.service;

import com.lms.courseservice.adapter.in.rest.dto.CourseCurriculumResponse;
import com.lms.courseservice.adapter.in.rest.dto.CourseResponse;
import com.lms.courseservice.adapter.in.rest.dto.CourseUpsertRequest;
import com.lms.courseservice.adapter.out.persistence.mapper.CourseMapper;
import com.lms.courseservice.application.port.in.ManageCourseUseCase;
import com.lms.courseservice.application.port.in.GetCourseUseCase;
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
    private final CourseMapper courseMapper;

    // =========================================================
    // SLUG GENERATION
    // =========================================================

    private String normalizeToAsciiSlug(String title) {
        if (title == null || title.isBlank()) {
            return "course";
        }
        String processed = title.replace("đ", "d").replace("Đ", "D");
        String normalized = Normalizer.normalize(processed, Normalizer.Form.NFD);
        String ascii = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return ascii.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private String generateSlug(String title) {
        String base = normalizeToAsciiSlug(title);
        if (base.isBlank()) {
            base = "course";
        }
        if (courseRepository.findBySlug(base).isEmpty()) {
            return base;
        }
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
    public List<CourseResponse> getAllPublishedCourses() {
        return courseMapper.toResponseList(courseRepository.findAllByStatus(CourseStatus.PUBLISHED));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseDetail(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND,"Course not found with slug: " + slug));
        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseCurriculumResponse getCurriculum(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND,"Course not found with id: " + courseId));
        return courseMapper.toCurriculumResponse(course);
    }

    // =========================================================
    // MANAGE COURSE USE CASES
    // =========================================================

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public CourseResponse createCourse(CourseUpsertRequest request) {
        String instructorId = SecurityUtils.getCurrentUserId();

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,"Category not found with id: " + request.getCategoryId()));
        }

        String slug = generateSlug(request.getTitle());
        Course course = Course.create(
                request.getTitle(),
                slug,
                request.getSummary(),
                request.getDescription(),
                request.getPrice(),
                request.getLevel(),
                request.getThumbnailUrl(),
                category,
                instructorId
        );
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public CourseResponse updateCourse(Long id, CourseUpsertRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND,"Course not found with id: " + id));

        SecurityUtils.checkOwnership(course.getInstructor());

        course.updateCourseInfo(
                request.getTitle(),
                request.getSummary(),
                request.getDescription(),
                request.getPrice(),
                request.getLevel(),
                request.getThumbnailUrl()
        );

        if(request.getCategoryId() != null){
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, "Category not found with ID: " + request.getCategoryId()));
            course.assignCategory(category);
        }
        if(request.getStatus() == CourseStatus.PUBLISHED){
            course.publish();
        } else if(request.getStatus() == CourseStatus.ARCHIVED){
            course.archive();
        }

        Course updatedCourse = courseRepository.save(course);
        return courseMapper.toResponse(updatedCourse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND,"Course not found with id: " + id));

        SecurityUtils.checkOwnership(course.getInstructor());

        courseRepository.deleteById(id);
    }
}