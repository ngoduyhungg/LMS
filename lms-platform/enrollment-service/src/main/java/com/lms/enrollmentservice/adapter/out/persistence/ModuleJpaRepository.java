package com.lms.courseservice.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.courseservice.domain.model.Module;

import java.util.List;

public interface ModuleJpaRepository extends JpaRepository<Module, Long> {
    List<Module> findAllByCourseIdOrderBySortOrder(Long courseId);
}
