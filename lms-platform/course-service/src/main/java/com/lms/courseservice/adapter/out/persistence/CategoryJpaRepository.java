package com.lms.courseservice.adapter.out.persistence;

import com.lms.courseservice.adapter.out.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {
    Optional<CategoryJpaEntity> findBySlug(String slug);
    List<CategoryJpaEntity> findAllByParentIsNull();
}