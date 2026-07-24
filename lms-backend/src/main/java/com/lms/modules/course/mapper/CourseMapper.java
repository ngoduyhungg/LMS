package com.lms.modules.course.mapper;

import com.lms.modules.auth.entity.User;
import com.lms.modules.course.dto.CourseCurriculumResponse;
import com.lms.modules.course.dto.CourseResponse;
import com.lms.modules.course.dto.CourseUpsertRequest;
import com.lms.modules.course.entity.Category;
import com.lms.modules.course.entity.Course;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ModuleMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface CourseMapper {

    @Mapping(source = "instructor.id", target = "instructorId")
    @Mapping(source = "instructor.fullName", target = "instructorName")
    CourseResponse toResponse(Course course);

    @Mapping(target = "modules", source = "modules")
    CourseCurriculumResponse toCurriculumResponse(Course course);
    List<CourseResponse> toResponseList(List<Course> courses);

    @Mapping(target = "id", ignore = true) // Bỏ qua ID vì tạo mới DB tự sinh
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "DRAFT") // Tự động gán status là DRAFT
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
    Course toEntity(CourseUpsertRequest request, Category category, User instructor, String slug);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "modules", ignore = true)
    void updateEntityFromRequest(CourseUpsertRequest request,@MappingTarget Course course);
}