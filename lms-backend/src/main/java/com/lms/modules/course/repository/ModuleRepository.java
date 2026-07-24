package com.lms.modules.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.modules.course.entity.Module;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findAllByCourseIdOrderBySortOrder(Long courseId);
}
