package com.lms.courseservice.application.port.in;

import com.lms.courseservice.adapter.in.rest.dto.CategoryResponse;

import java.util.List;

public interface GetCategoryUseCase {
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> getRootCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse getCategoryBySlug(String slug);
}
