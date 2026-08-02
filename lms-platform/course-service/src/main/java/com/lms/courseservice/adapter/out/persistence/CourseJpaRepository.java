package com.lms.courseservice.adapter.out.persistence;


import com.lms.courseservice.adapter.out.persistence.entity.CourseJpaEntity;
import com.lms.courseservice.domain.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseJpaRepository extends JpaRepository<CourseJpaEntity, Long> {
    Optional<CourseJpaEntity> findBySlug(String slug);

    List<CourseJpaEntity> findAllByStatus(CourseStatus status);
    @Query("SELECT c FROM CourseJpaEntity c LEFT JOIN FETCH c.modules m LEFT JOIN FETCH m.lessons WHERE c.id = :id")
    Optional<CourseJpaEntity> findByIdWithFullCurriculum(@Param("id") Long id);
}
