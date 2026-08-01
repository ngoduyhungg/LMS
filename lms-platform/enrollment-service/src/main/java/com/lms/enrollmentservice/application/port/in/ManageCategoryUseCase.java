package com.lms.courseservice.application.port.in;

import com.lms.courseservice.adapter.in.rest.dto.CategoryResponse;
import com.lms.courseservice.adapter.in.rest.dto.CategoryUpsertRequest;

public interface ManageCategoryUseCase {
    CategoryResponse createCategory(CategoryUpsertRequest request);
    CategoryResponse updateCategory(Long id, CategoryUpsertRequest request);
    void deleteCategory(Long id);
}
