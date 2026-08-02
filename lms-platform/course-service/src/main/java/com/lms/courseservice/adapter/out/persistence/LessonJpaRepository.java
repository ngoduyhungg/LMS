package com.lms.courseservice.adapter.out.persistence;

import com.lms.courseservice.adapter.out.persistence.entity.LessonJpaEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LessonJpaRepository extends JpaRepository<LessonJpaEntity, Long> {
    @EntityGraph(attributePaths = {"resources"})
    List<LessonJpaEntity> findAllByModuleIdOrderBySortOrder(Long moduleId);

    @NonNull
    @EntityGraph(attributePaths = {"resources"})
    Optional<LessonJpaEntity> findById(@NonNull Long id);
}