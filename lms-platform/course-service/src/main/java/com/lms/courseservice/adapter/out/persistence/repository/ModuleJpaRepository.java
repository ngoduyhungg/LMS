package com.lms.courseservice.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.courseservice.adapter.out.persistence.entity.ModuleJpaEntity;
import java.util.List;

public interface ModuleJpaRepository extends JpaRepository<ModuleJpaEntity, Long> {
    List<ModuleJpaEntity> findAllByCourseIdOrderBySortOrder(Long courseId);
}
