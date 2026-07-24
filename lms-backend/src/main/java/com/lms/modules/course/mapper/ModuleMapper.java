package com.lms.modules.course.mapper;

import com.lms.modules.course.dto.ModuleResponse;
import com.lms.modules.course.dto.ModuleUpsertRequest;
import com.lms.modules.course.entity.Course;
import com.lms.modules.course.entity.Module;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {LessonMapper.class}, builder = @Builder(disableBuilder = true))
public interface ModuleMapper {

    ModuleResponse toResponse(Module module);

    @Mapping(target = "id", ignore = true) // Bỏ qua ID vì DB tự sinh
    @Mapping(target = "lessons", ignore = true) // Chưa có chương nào được tạo ngay
    @Mapping(target = "course", source = "course") //Lấy tham số Course truyền vào gán cho Entity
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "sortOrder", source = "request.sortOrder", defaultValue = "0")
    Module toEntity(ModuleUpsertRequest request, Course course);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) // Tuyệt đối không được đè ID
    @Mapping(target = "course", ignore = true) // Không sửa quan hệ Course
    @Mapping(target = "lessons", ignore = true)
    void updateEntityFromRequest(ModuleUpsertRequest request, @MappingTarget Module module);
}