package com.lms.courseservice.adapter.in.rest.mapper;

import com.lms.courseservice.adapter.in.rest.dto.CourseCurriculumResponse;
import com.lms.courseservice.adapter.in.rest.dto.CourseResponse;
import com.lms.courseservice.adapter.in.rest.dto.CourseUpsertRequest;
import com.lms.courseservice.application.port.in.command.CourseCommand;
import com.lms.courseservice.domain.model.Course;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ModuleRestMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface CourseRestMapper {

    @Mapping(target = "instructorId", source = "instructor")
    @Mapping(target = "instructorName", ignore = true)
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    CourseResponse toResponse(Course course);

    @Mapping(target = "modules", source = "modules")
    CourseCurriculumResponse toCurriculumResponse(Course course);

    List<CourseResponse> toResponseList(List<Course> courses);
    CourseCommand toCommand(CourseUpsertRequest request);
}
