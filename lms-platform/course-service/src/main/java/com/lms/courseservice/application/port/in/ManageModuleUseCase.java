package com.lms.courseservice.application.port.in;
import com.lms.courseservice.application.port.in.command.ModuleCommand;
import com.lms.courseservice.domain.model.Module;

public interface ManageModuleUseCase {
    Module addModule(Long courseId, ModuleCommand request);
    Module updateModule(Long moduleId, ModuleCommand request);
    void deleteModule(Long moduleId);
}