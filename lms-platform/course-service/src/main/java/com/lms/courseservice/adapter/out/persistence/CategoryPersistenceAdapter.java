package com.lms.courseservice.adapter.out.persistence;

import com.lms.courseservice.adapter.out.persistence.entity.CategoryJpaEntity;
import com.lms.courseservice.adapter.out.persistence.mapper.CategoryPersistenceMapper;
import com.lms.courseservice.application.port.out.CategoryRepositoryPort;
import com.lms.courseservice.domain.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryPersistenceMapper mapper;

    @Override
    public Optional<Category> findById(Long id) {
        return categoryJpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override
    public Optional<Category> findBySlug(String slug) {
        return categoryJpaRepository.findBySlug(slug).map(mapper::toDomain);
    }
    @Override
    public List<Category> findAll(){
        return categoryJpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }
    @Override
    public List<Category> findAllByParentIsNull() {
        return categoryJpaRepository.findAllByParentIsNull().stream().map(mapper::toDomain).collect(Collectors.toList());
    }
    @Override
    public Category save(Category category){
        CategoryJpaEntity entity = mapper.toEntity(category);
        return mapper.toDomain(categoryJpaRepository.save(entity));
    }
    @Override
    public void deleteById(Long id){
        categoryJpaRepository.deleteById(id);
    }
    @Override
    public boolean existsBySlug(String slug){
        return categoryJpaRepository.findBySlug(slug).isPresent();
    }
    @Override
    public boolean existsById(Long id){
        return categoryJpaRepository.existsById(id);
    }
}
