package com.lms.courseservice.adapter.in.rest.mapper;

import com.lms.courseservice.adapter.in.rest.dto.ModuleResponse;
import com.lms.courseservice.adapter.in.rest.dto.ModuleUpsertRequest;
import com.lms.courseservice.application.port.in.command.ModuleCommand;
import com.lms.courseservice.domain.model.Module;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface ModuleRestMapper {
    ModuleCommand toCommand(ModuleUpsertRequest request);
    ModuleResponse toResponse(Module module);
    List<ModuleResponse> toResponseList(List<Module> modules);
}
