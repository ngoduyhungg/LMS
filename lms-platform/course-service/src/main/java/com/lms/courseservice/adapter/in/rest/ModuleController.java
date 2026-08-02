package com.lms.courseservice.adapter.in.rest;

import com.lms.courseservice.adapter.in.rest.dto.ModuleResponse;
import com.lms.courseservice.adapter.in.rest.dto.ModuleUpsertRequest;
import com.lms.courseservice.adapter.in.rest.mapper.ModuleRestMapper;
import com.lms.courseservice.application.port.in.GetModuleUseCase;
import com.lms.courseservice.application.port.in.ManageModuleUseCase;
import com.lms.courseservice.application.port.in.command.ModuleCommand;
import com.lms.courseservice.domain.model.Module;
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
    private final ModuleRestMapper restMapper;

    @GetMapping("/{courseId}/modules")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getModulesByCourseId(@PathVariable Long courseId){
        List<Module> modules = getModuleUseCase.getModulesByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Get modules successfully!", restMapper.toResponseList(modules)));
    }

    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<ApiResponse<ModuleResponse>> getModuleById(@PathVariable Long moduleId){
        Module module = getModuleUseCase.getModuleById(moduleId);
        return ResponseEntity.ok(ApiResponse.success("Get module details successfully!", restMapper.toResponse(module)));
    }

    @PostMapping("/{courseId}/modules")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<ModuleResponse>> addModule(@PathVariable Long courseId, @Valid @RequestBody ModuleUpsertRequest request){
        ModuleCommand command = restMapper.toCommand(request);
        Module module = manageModuleUseCase.addModule(courseId, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Module created successfully!", restMapper.toResponse(module)));
    }

    @PutMapping("/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(@PathVariable Long moduleId, @Valid @RequestBody ModuleUpsertRequest request){
        ModuleCommand command = restMapper.toCommand(request);
        Module module = manageModuleUseCase.updateModule(moduleId, command);
        return ResponseEntity.ok(ApiResponse.success("Module updated successfully!", restMapper.toResponse(module)));
    }

    @DeleteMapping("/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteModule(@PathVariable Long moduleId){
        manageModuleUseCase.deleteModule(moduleId);
        return ResponseEntity.ok(ApiResponse.success("Module deleted successfully!", null));
    }
}
