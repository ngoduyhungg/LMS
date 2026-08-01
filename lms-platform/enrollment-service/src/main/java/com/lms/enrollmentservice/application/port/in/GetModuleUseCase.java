package com.lms.courseservice.application.port.in;

import com.lms.courseservice.adapter.in.rest.dto.ModuleResponse;

import java.util.List;

public interface GetModuleUseCase {
    List<ModuleResponse> getModulesByCourseId(Long courseId);
    ModuleResponse getModuleById(Long moduleId);
}
