package com.lms.courseservice.application.service;

import com.lms.courseservice.adapter.in.rest.dto.CategoryResponse;
import com.lms.courseservice.adapter.in.rest.dto.CategoryUpsertRequest;
import com.lms.courseservice.adapter.out.persistence.mapper.CategoryMapper;
import com.lms.courseservice.application.port.in.GetCategoryUseCase;
import com.lms.courseservice.application.port.in.ManageCategoryUseCase;
import com.lms.courseservice.application.port.out.CategoryRepositoryPort;
import com.lms.courseservice.domain.model.Category;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import com.lms.shared.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryApplicationService implements GetCategoryUseCase, ManageCategoryUseCase {
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(){
        List<Category> categories = categoryRepositoryPort.findAll();
        return categoryMapper.toResponseList(categories);
    }
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories(){
        List<Category> categories = categoryRepositoryPort.findAllByParentIsNull();
        return categoryMapper.toResponseList(categories);
    }
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id){
        Category category = categoryRepositoryPort.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,"Category not found with ID: " + id));
        return categoryMapper.toResponse(category);
    }
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug){
        Category category = categoryRepositoryPort.findBySlug(slug).orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,"Category not found with slug: " + slug));
        return categoryMapper.toResponse(category);
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public CategoryResponse createCategory(CategoryUpsertRequest request){
        String generatedSlug = generateUniqueSlug(request.getName(), null);

        Category parent = null;
        if(request.getParentCategoryId() != null){
            parent = categoryRepositoryPort.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_SELF_PARENT, "Parent category not found with ID: " + request.getParentCategoryId()));
        }
        Category category = Category.create(request.getName(), generatedSlug, request.getDescription(), parent);
        Category savedCategory = categoryRepositoryPort.save(category);
        return categoryMapper.toResponse(savedCategory);
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public CategoryResponse updateCategory(Long id, CategoryUpsertRequest request){
        Category category = categoryRepositoryPort.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,"Category not found with ID: " + id));

        String newSlug = generateUniqueSlug(request.getName(), id);
        category.updateDetails(request.getName(), newSlug, request.getDescription());
        if(request.getParentCategoryId() != null){
            if(id.equals(request.getParentCategoryId())){
                Category parent = categoryRepositoryPort.findById(request.getParentCategoryId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_SELF_PARENT,"A category cannot be assigned as its own parent!"));
                category.assignParent(parent);
            }
        } else { category.assignParent(null);}

        Category updatedCategory = categoryRepositoryPort.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteCategory(Long id){
        if(!categoryRepositoryPort.existsById(id)){
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,"Category not found with ID: " + id);
        }
        categoryRepositoryPort.deleteById(id);
    }
    private String generateUniqueSlug(String name, Long currentCategoryId){
        String baseSlug = SlugUtils.toSlug(name);
        String slug = baseSlug;
        int counter = 1;

        while(true){
            Optional<Category> existingCategory = categoryRepositoryPort.findBySlug(slug);
            if(existingCategory.isEmpty() || (currentCategoryId != null && existingCategory.get().getId().equals(currentCategoryId))){
                break;
            }
            slug = baseSlug + "-" + counter;
            counter++;
        }
        return slug;
    }
}
