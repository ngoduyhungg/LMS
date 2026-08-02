package com.lms.courseservice.application.port.out;

import com.lms.courseservice.domain.enums.CourseStatus;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Lesson;
import com.lms.courseservice.domain.model.Module;

import java.util.List;
import java.util.Optional;

/**
 * Port (outbound) — định nghĩa hợp đồng lưu trữ cho Course.
 * Tầng Application Service chỉ được phép giao tiếp qua interface này,
 * tuyệt đối không import bất cứ thứ gì từ org.springframework.data.jpa.
 */
public interface CourseRepositoryPort {

    Course save(Course course);

    Optional<Course> findById(Long id);

    Optional<Course> findBySlug(String slug);

    /**
     * Tìm tất cả khóa học theo trạng thái (DRAFT / PUBLISHED / ARCHIVED).
     * Bổ sung để thay thế việc gọi thẳng CourseJpaRepository.findAllByStatus() trong service.
     */
    List<Course> findAllByStatus(CourseStatus status);

    List<Course> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

    Optional<Course> findByIdWithFullCurriculum(Long id);
    Optional<Module> findModuleById(Long moduleId);
    Optional<Lesson> findLessonById(Long lessonId);
}