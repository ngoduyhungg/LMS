package com.lms.courseservice.application.port.out;

import com.lms.courseservice.domain.model.Module;
import java.util.List;
import java.util.Optional;

public interface ModuleRepositoryPort {
    Module save(Module module);
    Optional<Module> findById(Long id);
    List<Module> findAllByCourseIdOrderBySortOrder(Long courseId);
    void deleteById(Long id);
}