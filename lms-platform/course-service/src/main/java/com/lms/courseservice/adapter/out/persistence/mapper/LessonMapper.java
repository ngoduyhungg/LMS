package com.lms.courseservice.adapter.out.persistence.mapper;

import com.lms.courseservice.adapter.in.rest.dto.LessonResponse;
import com.lms.courseservice.adapter.in.rest.dto.LessonUpsertRequest;
import com.lms.courseservice.domain.model.Lesson;
import com.lms.courseservice.domain.model.Module;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface LessonMapper {

    LessonResponse toResponse(Lesson lesson);

    List<LessonResponse> toResponseList(List<Lesson> lessons);
    @Mapping(target = "id", ignore = true) // Bỏ qua ID vì DB tự sinh
    @Mapping(target = "module", source = "module") // Gán object Module vào Lesson
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "content", source = "request.content")
    @Mapping(target = "videoUrl", source = "request.videoUrl")
    @Mapping(target = "durationSeconds", source = "request.durationSeconds", defaultValue = "0") // Nếu null tự về 0
    @Mapping(target = "lessonType", source = "request.lessonType")
    @Mapping(target = "isPreview", source = "request.isPreview", defaultValue = "false")   // Nếu null tự về false
    @Mapping(target = "sortOrder", source = "request.sortOrder", defaultValue = "0")       // Nếu null tự về 0
    Lesson toEntity(LessonUpsertRequest request, Module module);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) // Tuyệt đối không được đè ID
    @Mapping(target = "module", ignore = true) // Không sửa quan hệ Module
    @Mapping(target = "resources", ignore = true)
    void updateEntityFromRequest(LessonUpsertRequest request, @MappingTarget Lesson lesson);
}