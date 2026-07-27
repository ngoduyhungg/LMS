// package: com.lms.courseservice.adapter.out.persistence
package com.lms.courseservice.adapter.out.persistence;

import com.lms.courseservice.application.port.out.ModuleRepositoryPort;
import com.lms.courseservice.domain.model.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence Adapter — tầng duy nhất được phép inject ModuleJpaRepository.
 *
 * Tuân thủ Hexagonal Architecture (Ports & Adapters):
 *   - Implement ModuleRepositoryPort (port outbound).
 *   - Tầng Application Service chỉ biết đến ModuleRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class ModulePersistenceAdapter implements ModuleRepositoryPort {

    private final ModuleJpaRepository moduleJpaRepository;

    @Override
    public Module save(Module module) {
        return moduleJpaRepository.save(module);
    }

    @Override
    public Optional<Module> findById(Long id) {
        return moduleJpaRepository.findById(id);
    }

    @Override
    public List<Module> findAllByCourseIdOrderBySortOrder(Long courseId) {
        return moduleJpaRepository.findAllByCourseIdOrderBySortOrder(courseId);
    }

    @Override
    public void deleteById(Long id) {
        moduleJpaRepository.deleteById(id);
    }
}
