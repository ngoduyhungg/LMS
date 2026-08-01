package com.lms.courseservice.adapter.out.persistence;

import com.lms.courseservice.domain.model.Lesson;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonJpaRepository extends JpaRepository<Lesson, Long> {

    @EntityGraph(attributePaths = {"resources"})
    List<Lesson> findAllByModuleIdOrderBySortOrder(Long moduleId);

    @EntityGraph(attributePaths = {"resources"})
    Optional<Lesson> findById(@NonNull Long id);
}