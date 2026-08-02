package com.lms.courseservice.application.port.in;

import com.lms.courseservice.application.port.in.command.CategoryCommand;
import com.lms.courseservice.domain.model.Category;

public interface ManageCategoryUseCase {
    Category createCategory(CategoryCommand request);
    Category updateCategory(Long id, CategoryCommand request);
    void deleteCategory(Long id);
}
