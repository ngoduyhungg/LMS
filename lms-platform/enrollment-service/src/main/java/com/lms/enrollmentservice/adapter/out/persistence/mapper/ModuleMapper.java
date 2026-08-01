package com.lms.courseservice.adapter.out.persistence.mapper;


import com.lms.courseservice.adapter.in.rest.dto.ModuleResponse;
import com.lms.courseservice.domain.model.Module;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {LessonMapper.class}, builder = @Builder(disableBuilder = true))
public interface ModuleMapper {

    ModuleResponse toResponse(Module module);
    List<ModuleResponse> toResponseList(List<Module> modules);
}