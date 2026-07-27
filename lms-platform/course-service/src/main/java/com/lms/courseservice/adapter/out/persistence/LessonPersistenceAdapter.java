// package: com.lms.courseservice.adapter.out.persistence
package com.lms.courseservice.adapter.out.persistence;

import com.lms.courseservice.application.port.out.LessonRepositoryPort;
import com.lms.courseservice.domain.model.Lesson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence Adapter — tầng duy nhất được phép inject LessonJpaRepository.
 *
 * Tuân thủ Hexagonal Architecture (Ports & Adapters):
 *   - Implement LessonRepositoryPort (port outbound).
 *   - Tầng Application Service chỉ biết đến LessonRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class LessonPersistenceAdapter implements LessonRepositoryPort {

    private final LessonJpaRepository lessonJpaRepository;

    @Override
    public Lesson save(Lesson lesson) {
        return lessonJpaRepository.save(lesson);
    }

    @Override
    public Optional<Lesson> findById(Long id) {
        return lessonJpaRepository.findById(id);
    }

    @Override
    public List<Lesson> findAllByModuleIdOrderBySortOrder(Long moduleId) {
        return lessonJpaRepository.findAllByModuleIdOrderBySortOrder(moduleId);
    }

    @Override
    public void deleteById(Long id) {
        lessonJpaRepository.deleteById(id);
    }
}
