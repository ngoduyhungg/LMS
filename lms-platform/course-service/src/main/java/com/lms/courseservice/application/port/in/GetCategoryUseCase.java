package com.lms.courseservice.application.port.in;

import com.lms.courseservice.domain.model.Category;
import java.util.List;

public interface GetCategoryUseCase {
    List<Category> getAllCategories();
    List<Category> getRootCategories();
    Category getCategoryById(Long id);
    Category getCategoryBySlug(String slug);
}
