package com.lms.courseservice.application.port.in;
import com.lms.courseservice.domain.model.Module;
import java.util.List;

public interface GetModuleUseCase {
    List<Module> getModulesByCourseId(Long courseId);
    Module getModuleById(Long moduleId);
}
