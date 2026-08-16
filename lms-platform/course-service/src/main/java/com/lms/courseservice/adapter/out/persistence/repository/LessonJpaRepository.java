package com.lms.courseservice.adapter.out.persistence.repository;

import com.lms.courseservice.adapter.out.persistence.entity.LessonJpaEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonJpaRepository extends JpaRepository<LessonJpaEntity, Long> {
    @EntityGraph(attributePaths = {"resources"})
    List<LessonJpaEntity> findAllByModuleIdOrderBySortOrder(Long moduleId);

    @NonNull
    @EntityGraph(attributePaths = {"resources"})
    Optional<LessonJpaEntity> findById(@NonNull Long id);
    @Query("SELECT COUNT(l) FROM LessonJpaEntity l WHERE l.module.course.id = :courseId")
    long countLessonsByCourseId(@Param("courseId") Long courseId);
    @Query("SELECT COUNT(l) > 0 FROM LessonJpaEntity l WHERE l.id = :lessonId AND l.module.course.id = :courseId")
    boolean existsByIdAndModuleCourseId(@Param("lessonId") Long lessonId, @Param("courseId") Long courseId);
}