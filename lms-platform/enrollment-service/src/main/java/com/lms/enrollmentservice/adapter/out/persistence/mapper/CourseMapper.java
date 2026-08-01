package com.lms.courseservice.adapter.out.persistence.mapper;

import com.lms.courseservice.adapter.in.rest.dto.CourseCurriculumResponse;
import com.lms.courseservice.adapter.in.rest.dto.CourseResponse;
import com.lms.courseservice.domain.model.Course;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = {ModuleMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    builder = @Builder(disableBuilder = true)
)
public interface CourseMapper {

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
}