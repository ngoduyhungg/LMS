package com.lms.courseservice.adapter.in.rest;

import com.lms.courseservice.adapter.in.rest.dto.CategoryResponse;
import com.lms.courseservice.adapter.in.rest.dto.CategoryUpsertRequest;
import com.lms.courseservice.adapter.in.rest.mapper.CategoryRestMapper;
import com.lms.courseservice.application.port.in.GetCategoryUseCase;
import com.lms.courseservice.application.port.in.ManageCategoryUseCase;
import com.lms.courseservice.application.port.in.command.CategoryCommand;
import com.lms.courseservice.domain.model.Category;
import com.lms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final GetCategoryUseCase getCategoryUseCase;
    private final ManageCategoryUseCase manageCategoryUseCase;
    private final CategoryRestMapper restMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(){
        List<Category> categories = getCategoryUseCase.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Get list successfully!", restMapper.toResponseList(categories)));
    }

    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories(){
        List<Category> categories = getCategoryUseCase.getRootCategories();
        return ResponseEntity.ok(ApiResponse.success("Get root categories successfully!", restMapper.toResponseList(categories)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        Category category = getCategoryUseCase.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Get category by ID successfully!", restMapper.toResponse(category)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        Category category = getCategoryUseCase.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Get category by slug successfully!", restMapper.toResponse(category)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryUpsertRequest request) {
        CategoryCommand command = restMapper.toCommand(request);
        Category category = manageCategoryUseCase.createCategory(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(restMapper.toResponse(category)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryUpsertRequest request) {
        CategoryCommand command = restMapper.toCommand(request);
        Category category = manageCategoryUseCase.updateCategory(id, command);
        return ResponseEntity.ok(ApiResponse.success(restMapper.toResponse(category)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        manageCategoryUseCase.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
