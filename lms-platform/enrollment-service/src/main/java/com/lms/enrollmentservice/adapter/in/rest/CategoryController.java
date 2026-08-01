package com.lms.courseservice.adapter.in.rest;

import com.lms.courseservice.adapter.in.rest.dto.CategoryResponse;
import com.lms.courseservice.adapter.in.rest.dto.CategoryUpsertRequest;
import com.lms.courseservice.application.port.in.GetCategoryUseCase;
import com.lms.courseservice.application.port.in.ManageCategoryUseCase;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(){
        return ResponseEntity.ok(ApiResponse.success("Get list successfully!", getCategoryUseCase.getAllCategories()));
    }
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories(){
        return ResponseEntity.ok(ApiResponse.success("Get root categories successfully!", getCategoryUseCase.getRootCategories()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Get category by ID successfully!", getCategoryUseCase.getCategoryById(id)));
    }
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success("Get category by slug successfully!", getCategoryUseCase.getCategoryBySlug(slug)));
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(manageCategoryUseCase.createCategory(request)));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryUpsertRequest request) {
        return ResponseEntity.ok(ApiResponse.success(manageCategoryUseCase.updateCategory(id, request)));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        manageCategoryUseCase.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
