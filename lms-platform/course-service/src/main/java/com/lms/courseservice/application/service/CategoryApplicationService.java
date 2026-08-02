package com.lms.courseservice.application.service;

import com.lms.courseservice.application.port.in.GetCategoryUseCase;
import com.lms.courseservice.application.port.in.ManageCategoryUseCase;
import com.lms.courseservice.application.port.in.command.CategoryCommand;
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

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories(){
        return categoryRepositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getRootCategories(){
        return categoryRepositoryPort.findAllByParentIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id){
        return categoryRepositoryPort.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,"Category not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryBySlug(String slug){
        return categoryRepositoryPort.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,"Category not found with slug: " + slug));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Category createCategory(CategoryCommand request){
        String generatedSlug = generateUniqueSlug(request.name(), null);

        Category parent = null;
        if(request.parentCategoryId() != null){
            parent = categoryRepositoryPort.findById(request.parentCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_SELF_PARENT, "Parent category not found with ID: " + request.parentCategoryId()));
        }
        Category category = Category.create(request.name(), generatedSlug, request.description(), parent);
        return categoryRepositoryPort.save(category);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Category updateCategory(Long id, CategoryCommand request){
        Category category = categoryRepositoryPort.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,"Category not found with ID: " + id));

        String newSlug = generateUniqueSlug(request.name(), id);
        category.updateDetails(request.name(), newSlug, request.description());

        if(request.parentCategoryId() != null){
            // Logic gốc: lấy parent từ db lên gán vào
            if(!id.equals(request.parentCategoryId())){
                Category parent = categoryRepositoryPort.findById(request.parentCategoryId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,"Parent category not found"));
                category.assignParent(parent);
            } else {
                throw new BusinessException(ErrorCode.CATEGORY_SELF_PARENT,"A category cannot be assigned as its own parent!");
            }
        } else {
            category.assignParent(null);
        }

        return categoryRepositoryPort.save(category);
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
