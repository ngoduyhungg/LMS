// package: com.lms.courseservice.adapter.out.persistence.mapper
package com.lms.courseservice.adapter.out.persistence.mapper;

import com.lms.courseservice.adapter.in.rest.dto.CourseCurriculumResponse;
import com.lms.courseservice.adapter.in.rest.dto.CourseResponse;
import com.lms.courseservice.adapter.in.rest.dto.CourseUpsertRequest;
import com.lms.courseservice.domain.model.Category;
import com.lms.courseservice.domain.model.Course;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct Mapper cho Course entity.
 *
 * Fix lỗi critical:
 *   - toResponse: target "instructorId" ← source "instructor" (String → String, không bị null)
 *
 * Fix tính năng:
 *   - updateEntityFromRequest: KHÔNG ignore status nữa, kết hợp với
 *     NullValuePropertyMappingStrategy.IGNORE để PUT có thể đổi status sang PUBLISHED
 *     trong khi POST luôn tạo với status = DRAFT (qua constant trong toEntity).
 */
@Mapper(
    componentModel = "spring",
    uses = {ModuleMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    builder = @Builder(disableBuilder = true)
)
public interface CourseMapper {

    /**
     * Ánh xạ Course entity → CourseResponse DTO.
     *
     * BUG FIX: target phải là "instructorId" (tên field trong CourseResponse),
     *          source là "instructor" (tên field trong Course entity).
     * instructorName bỏ qua vì course-service không quản lý bảng User.
     */
    @Mapping(target = "instructorId", source = "instructor")
    @Mapping(target = "instructorName", ignore = true)
    CourseResponse toResponse(Course course);

    /**
     * Ánh xạ Course entity → CourseCurriculumResponse DTO (bao gồm modules & lessons).
     */
    @Mapping(target = "modules", source = "modules")
    CourseCurriculumResponse toCurriculumResponse(Course course);

    /**
     * Ánh xạ danh sách Course entity → danh sách CourseResponse DTO.
     */
    List<CourseResponse> toResponseList(List<Course> courses);

    /**
     * Tạo Course entity mới từ request.
     *
     * - status luôn là "DRAFT" khi tạo mới (constant).
     * - instructor và slug được truyền vào từ service (không lấy từ request).
     * - category được resolve trước trong service rồi truyền vào.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "summary", source = "request.summary")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "thumbnailUrl", source = "request.thumbnailUrl")
    @Mapping(target = "price", source = "request.price")
    @Mapping(target = "level", source = "request.level")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "instructor", source = "instructor")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "modules", ignore = true)
    Course toEntity(CourseUpsertRequest request, Category category, String instructor, String slug);

    /**
     * Cập nhật Course entity từ request (dùng cho PUT).
     *
     * NullValuePropertyMappingStrategy.IGNORE đảm bảo:
     *   - Nếu request.status = null → status entity KHÔNG bị ghi đè (an toàn).
     *   - Nếu request.status = "PUBLISHED" → status được cập nhật thành PUBLISHED.
     * instructor, category, slug, id KHÔNG được phép thay đổi qua API này.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "modules", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(CourseUpsertRequest request, @MappingTarget Course course);
}