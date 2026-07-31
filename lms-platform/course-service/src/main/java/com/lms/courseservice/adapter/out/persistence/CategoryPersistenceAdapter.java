package com.lms.courseservice.adapter.out.persistence;

import com.lms.courseservice.application.port.out.CategoryRepositoryPort;
import com.lms.courseservice.domain.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence Adapter — tầng duy nhất được phép inject CategoryJpaRepository.
 *
 * Tuân thủ Hexagonal Architecture (Ports & Adapters):
 *   - Implement CategoryRepositoryPort (port outbound mới tạo).
 *   - Tầng Application Service chỉ biết đến CategoryRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Optional<Category> findById(Long id) {
        return categoryJpaRepository.findById(id);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return categoryJpaRepository.findBySlug(slug);
    }

    @Override
    public List<Category> findAll(){ return categoryJpaRepository.findAll();}
    @Override
    public List<Category> findAllByParentIsNull() { return categoryJpaRepository.findAllByParentIsNull();}
    @Override
    public Category save(Category category){ return categoryJpaRepository.save(category);}
    @Override
    public void deleteById(Long id){ categoryJpaRepository.deleteById(id);}
    @Override
    public boolean existsBySlug(String slug){ return categoryJpaRepository.findBySlug(slug).isPresent();}
    @Override
    public boolean existsById(Long id){ return categoryJpaRepository.existsById(id);}
}
