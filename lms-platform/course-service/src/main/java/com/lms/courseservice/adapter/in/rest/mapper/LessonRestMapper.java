package com.lms.courseservice.adapter.in.rest.mapper;

import com.lms.courseservice.adapter.in.rest.dto.LessonResponse;
import com.lms.courseservice.adapter.in.rest.dto.LessonUpsertRequest;
import com.lms.courseservice.application.port.in.command.LessonCommand;
import com.lms.courseservice.domain.model.Lesson;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", uses = {LessonResourceRestMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface LessonRestMapper {
    LessonCommand toCommand(LessonUpsertRequest request);
    LessonResponse toResponse(Lesson lesson);
    List<LessonResponse> toResponseList(List<Lesson> lessons);
}
