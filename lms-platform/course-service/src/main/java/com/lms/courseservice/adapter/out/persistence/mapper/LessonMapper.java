package com.lms.courseservice.adapter.out.persistence.mapper;

import com.lms.courseservice.adapter.in.rest.dto.LessonResponse;
import com.lms.courseservice.domain.model.Lesson;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface LessonMapper {

    LessonResponse toResponse(Lesson lesson);

    List<LessonResponse> toResponseList(List<Lesson> lessons);
}