// package: com.lms.courseservice.application.port.in
package com.lms.courseservice.application.port.in;

import com.lms.courseservice.adapter.in.rest.dto.CourseCurriculumResponse;
import com.lms.courseservice.adapter.in.rest.dto.CourseResponse;

import java.util.List;

/**
 * Port (inbound) — chỉ chứa các hành vi QUERY (Read-only).
 * Tách biệt hoàn toàn khỏi các mutation use case theo ISP (Interface Segregation Principle).
 * - Create/Update/Delete Course → CreateCourseUseCase
 * - Add/Update/Delete Module   → ManageModuleUseCase
 * - Add/Update/Delete Lesson   → ManageLessonUseCase
 */
public interface GetCourseUseCase {

    /**
     * Trả về danh sách tất cả khóa học có trạng thái PUBLISHED.
     */
    List<CourseResponse> getAllPublishedCourses();

    /**
     * Trả về chi tiết một khóa học theo slug SEO-friendly.
     */
    CourseResponse getCourseDetail(String slug);

    /**
     * Trả về cấu trúc chương trình học (modules & lessons) của một khóa học.
     */
    CourseCurriculumResponse getCurriculum(Long courseId);
}