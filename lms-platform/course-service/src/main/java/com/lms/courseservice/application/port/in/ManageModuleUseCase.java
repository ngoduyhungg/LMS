package com.lms.courseservice.application.port.in;

import com.lms.courseservice.adapter.in.rest.dto.ModuleResponse;
import com.lms.courseservice.adapter.in.rest.dto.ModuleUpsertRequest;

public interface ManageModuleUseCase {
    ModuleResponse addModule(Long courseId, ModuleUpsertRequest request);
    ModuleResponse updateModule(Long moduleId, ModuleUpsertRequest request);
    void deleteModule(Long moduleId);
}