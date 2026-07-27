package com.lms.courseservice.application.service;

import com.lms.courseservice.adapter.in.rest.dto.*;
import com.lms.courseservice.adapter.out.persistence.mapper.CourseMapper;
import com.lms.courseservice.adapter.out.persistence.mapper.LessonMapper;
import com.lms.courseservice.adapter.out.persistence.mapper.ModuleMapper;
import com.lms.courseservice.application.port.in.CreateCourseUseCase;
import com.lms.courseservice.application.port.in.GetCourseUseCase;
import com.lms.courseservice.application.port.in.ManageLessonUseCase;
import com.lms.courseservice.application.port.in.ManageModuleUseCase;
import com.lms.courseservice.application.port.out.CategoryRepositoryPort;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.courseservice.application.port.out.LessonRepositoryPort;
import com.lms.courseservice.application.port.out.ModuleRepositoryPort;
import com.lms.courseservice.domain.enums.CourseStatus;
import com.lms.courseservice.domain.model.Category;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Lesson;
import com.lms.courseservice.domain.model.Module;
import com.lms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Application Service — triển khai toàn bộ Use Case của Course, Module, Lesson.
 *
 * Nguyên tắc bắt buộc tuân thủ (Hexagonal Architecture):
 *   ✅ CHỈ inject và giao tiếp qua các interface Port (CourseRepositoryPort, v.v.)
 *   ❌ TUYỆT ĐỐI KHÔNG import org.springframework.data.jpa hay bất kỳ *JpaRepository nào
 *
 * Bảo mật (IDOR prevention):
 *   - Mọi thao tác mutate đều kiểm tra quyền sở hữu (ownership check).
 *   - ROLE_ADMIN được bypass kiểm tra.
 *   - ROLE_INSTRUCTOR chỉ được sửa/xóa tài nguyên của chính mình.
 */
@Service
@RequiredArgsConstructor
public class CourseApplicationService
        implements GetCourseUseCase, CreateCourseUseCase, ManageModuleUseCase, ManageLessonUseCase {

    // =========================================================
    // PORTS — Giao tiếp với tầng persistence qua interface Port.
    // Không có bất kỳ import JPA nào bên dưới.
    // =========================================================
    private final CourseRepositoryPort courseRepository;
    private final ModuleRepositoryPort moduleRepository;
    private final LessonRepositoryPort lessonRepository;
    private final CategoryRepositoryPort categoryRepository;

    // MapStruct Mappers
    private final CourseMapper courseMapper;
    private final ModuleMapper moduleMapper;
    private final LessonMapper lessonMapper;

    // =========================================================
    // SLUG GENERATION (SEO-Friendly, Vietnamese Support)
    // =========================================================

    /**
     * Chuẩn hoá tiêu đề thành base slug ASCII.
     * Dùng java.text.Normalizer để phân giải ký tự có dấu thành dạng cơ sở + dấu phụ (NFD),
     * sau đó loại bỏ dấu phụ (InCombiningDiacriticalMarks).
     * Ký tự đặc biệt 'đ'/'Đ' được xử lý riêng vì không phân giải theo chuẩn NFD thông thường.
     */
    private String normalizeToAsciiSlug(String title) {
        if (title == null || title.isBlank()) {
            return "course";
        }
        // Bước 1: Xử lý 'đ' và 'Đ' riêng vì NFD không decompose chúng
        String processed = title.replace("đ", "d").replace("Đ", "D");
        // Bước 2: Chuẩn hoá NFD — tách ký tự cơ sở và dấu phụ
        String normalized = Normalizer.normalize(processed, Normalizer.Form.NFD);
        // Bước 3: Xoá toàn bộ dấu phụ (combining diacritical marks)
        String ascii = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // Bước 4: Chuyển thành lowercase, xoá ký tự không hợp lệ, thay khoảng trắng = dấu -
        return ascii.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Sinh slug SEO-friendly cho Course.
     *
     * Thuật toán:
     *   1. Chuẩn hoá title → base slug (ví dụ: "Lập Trình Java" → "lap-trinh-java").
     *   2. Nếu slug chưa tồn tại trong DB → dùng ngay.
     *   3. Nếu đã tồn tại → append 4 ký tự ngẫu nhiên từ UUID (ví dụ: "lap-trinh-java-a3f9").
     *   4. Lặp cho đến khi tìm được slug duy nhất (cực kỳ hiếm xảy ra ở bước 3+).
     *
     * TUYỆT ĐỐI KHÔNG dùng System.currentTimeMillis() — URL quá dài, xấu, không SEO.
     */
    private String generateSlug(String title) {
        String base = normalizeToAsciiSlug(title);
        if (base.isBlank()) {
            base = "course";
        }
        // Kiểm tra slug base trước
        if (courseRepository.findBySlug(base).isEmpty()) {
            return base;
        }
        // Slug đã tồn tại → append suffix ngẫu nhiên 4 ký tự
        String candidate;
        do {
            String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 4);
            candidate = base + "-" + suffix;
        } while (courseRepository.findBySlug(candidate).isPresent());

        return candidate;
    }

    // =========================================================
    // SECURITY — IDOR Prevention (Ownership Check)
    // =========================================================

    /**
     * Trích xuất JWT từ SecurityContext và trả về subject (UUID của user hiện tại).
     */
    private String getCurrentUserId() {
        JwtAuthenticationToken auth =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth.getToken().getSubject();
    }

    /**
     * Kiểm tra xem user hiện tại có quyền ROLE_ADMIN không.
     * Nếu có ADMIN, bypass kiểm tra ownership.
     */
    private boolean isAdmin() {
        JwtAuthenticationToken auth =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Kiểm tra quyền sở hữu tài nguyên.
     *
     * Logic:
     *   - Nếu user là ADMIN → bỏ qua, không cần kiểm tra.
     *   - Nếu user là INSTRUCTOR (hoặc bất kỳ role nào khác) → UUID phải khớp với instructorId.
     *   - Không khớp → ném AccessDeniedException (HTTP 403 Forbidden).
     *
     * @param resourceInstructorId UUID của giảng viên sở hữu tài nguyên (từ entity).
     */
    private void checkOwnership(String resourceInstructorId) {
        if (isAdmin()) {
            return; // Admin được phép thao tác mọi tài nguyên
        }
        String currentUserId = getCurrentUserId();
        if (!currentUserId.equals(resourceInstructorId)) {
            throw new AccessDeniedException(
                    "Access Denied: You do not have permission to modify this resource. " +
                    "Only the resource owner or an ADMIN can perform this action.");
        }
    }

    // =========================================================
    // GET USE CASES (Read-only)
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with slug: " + slug));
        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseCurriculumResponse getCurriculum(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with id: " + courseId));
        return courseMapper.toCurriculumResponse(course);
    }

    // =========================================================
    // COURSE CRUD USE CASES
    // =========================================================

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public CourseResponse createCourse(CourseUpsertRequest request) {
        // Lấy instructorId trực tiếp từ JWT Token (không truy vấn DB User)
        String instructorId = getCurrentUserId();

        // Resolve Category nếu có (nullable)
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + request.getCategoryId()));
        }

        // Sinh slug SEO-friendly từ title (tiếng Việt → ASCII, không dùng timestamp)
        String slug = generateSlug(request.getTitle());

        Course course = courseMapper.toEntity(request, category, instructorId, slug);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public CourseResponse updateCourse(Long id, CourseUpsertRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with id: " + id));

        // IDOR Check: Instructor chỉ được sửa khóa học của chính mình
        checkOwnership(course.getInstructor());

        // Cập nhật các trường từ request (bao gồm status nếu có)
        courseMapper.updateEntityFromRequest(request, course);

        // Cập nhật category nếu request có gửi categoryId
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + request.getCategoryId()));
            course.setCategory(category);
        }

        Course updatedCourse = courseRepository.save(course);
        return courseMapper.toResponse(updatedCourse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with id: " + id));

        // IDOR Check: Instructor chỉ được xóa khóa học của chính mình
        checkOwnership(course.getInstructor());

        courseRepository.deleteById(id);
    }

    // =========================================================
    // MODULE CRUD USE CASES
    // =========================================================

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ModuleResponse addModule(Long courseId, ModuleUpsertRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with id: " + courseId));

        // Chỉ giảng viên sở hữu khóa học mới được thêm module
        checkOwnership(course.getInstructor());

        Module module = moduleMapper.toEntity(request, course);
        return moduleMapper.toResponse(moduleRepository.save(module));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ModuleResponse updateModule(Long moduleId, ModuleUpsertRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Module not found with id: " + moduleId));

        // IDOR Check: Truy ngược lên Course để lấy instructorId
        checkOwnership(module.getCourse().getInstructor());

        moduleMapper.updateEntityFromRequest(request, module);
        return moduleMapper.toResponse(moduleRepository.save(module));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void deleteModule(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Module not found with id: " + moduleId));

        // IDOR Check: Truy ngược lên Course để lấy instructorId
        checkOwnership(module.getCourse().getInstructor());

        moduleRepository.deleteById(moduleId);
    }

    // =========================================================
    // LESSON CRUD USE CASES
    // =========================================================

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public LessonResponse addLesson(Long moduleId, LessonUpsertRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Module not found with id: " + moduleId));

        // Chỉ giảng viên sở hữu course mới được thêm lesson
        checkOwnership(module.getCourse().getInstructor());

        Lesson lesson = lessonMapper.toEntity(request, module);
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public LessonResponse updateLesson(Long lessonId, LessonUpsertRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lesson not found with id: " + lessonId));

        // IDOR Check: Truy ngược Lesson → Module → Course → instructorId
        checkOwnership(lesson.getModule().getCourse().getInstructor());

        lessonMapper.updateEntityFromRequest(request, lesson);
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lesson not found with id: " + lessonId));

        // IDOR Check: Truy ngược Lesson → Module → Course → instructorId
        checkOwnership(lesson.getModule().getCourse().getInstructor());

        lessonRepository.deleteById(lessonId);
    }
}