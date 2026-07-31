package com.lms.courseservice.application.port.out;

import com.lms.courseservice.domain.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Port (outbound) — định nghĩa hợp đồng lưu trữ cho Category.
 * Tạo mới để tuân thủ Hexagonal Architecture:
 * Service không được phép inject CategoryJpaRepository trực tiếp.
 */
public interface CategoryRepositoryPort {

    Optional<Category> findById(Long id);

    Optional<Category> findBySlug(String slug);
    List<Category> findAll();
    List<Category> findAllByParentIsNull();
    Category save(Category category);
    void deleteById(Long id);
    boolean existsBySlug(String slug);
    boolean existsById(Long id);
}
