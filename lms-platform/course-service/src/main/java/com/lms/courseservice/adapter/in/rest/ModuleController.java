package com.lms.courseservice.adapter.in.rest;

import com.lms.courseservice.adapter.in.rest.dto.ModuleResponse;
import com.lms.courseservice.adapter.in.rest.dto.ModuleUpsertRequest;
import com.lms.courseservice.application.port.in.GetModuleUseCase;
import com.lms.courseservice.application.port.in.ManageModuleUseCase;
import com.lms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class ModuleController {
    private final GetModuleUseCase getModuleUseCase;
    private final ManageModuleUseCase manageModuleUseCase;

    @GetMapping("/{courseId}/modules")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getModulesByCourseId(@PathVariable Long courseId){
        return ResponseEntity.ok(ApiResponse.success("Get modules successfully!", getModuleUseCase.getModulesByCourseId(courseId)));
    }
    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<ApiResponse<ModuleResponse>> getModuleById(@PathVariable Long moduleId){
        return ResponseEntity.ok(ApiResponse.success("Get module details successfully!", getModuleUseCase.getModuleById(moduleId)));
    }

    @PostMapping("/{courseId}/modules")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<ModuleResponse>> addModule(@PathVariable Long courseId, @Valid @RequestBody ModuleUpsertRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Module created successfully!", manageModuleUseCase.addModule(courseId,request)));
    }

    @PutMapping("/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(@PathVariable Long moduleId, @Valid @RequestBody ModuleUpsertRequest request){
        return ResponseEntity.ok(ApiResponse.success("Module updated successfully!", manageModuleUseCase.updateModule(moduleId, request)));
    }

    @DeleteMapping("/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteModule(@PathVariable Long moduleId){
        manageModuleUseCase.deleteModule(moduleId);
        return ResponseEntity.ok(ApiResponse.success("Module deleted successfully!", null));
    }

}
