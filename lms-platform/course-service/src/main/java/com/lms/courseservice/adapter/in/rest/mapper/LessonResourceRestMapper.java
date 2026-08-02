package com.lms.courseservice.adapter.in.rest.mapper;

import com.lms.courseservice.adapter.in.rest.dto.LessonResourceResponse;
import com.lms.courseservice.domain.model.LessonResource;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface LessonResourceRestMapper {
    LessonResourceResponse toResponse(LessonResource resource);
    List<LessonResourceResponse> toResponseList(List<LessonResource> resources);
}
