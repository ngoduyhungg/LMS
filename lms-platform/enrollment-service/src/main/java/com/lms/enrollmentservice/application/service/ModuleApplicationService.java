package com.lms.courseservice.application.service;

import com.lms.courseservice.adapter.in.rest.dto.ModuleResponse;
import com.lms.courseservice.adapter.in.rest.dto.ModuleUpsertRequest;
import com.lms.courseservice.adapter.out.persistence.mapper.ModuleMapper;
import com.lms.courseservice.application.port.in.GetModuleUseCase;
import com.lms.courseservice.application.port.in.ManageModuleUseCase;
import com.lms.courseservice.application.port.out.CourseRepositoryPort;
import com.lms.courseservice.application.port.out.ModuleRepositoryPort;
import com.lms.security.util.SecurityUtils;
import com.lms.courseservice.domain.model.Course;
import com.lms.courseservice.domain.model.Module;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application Service quản lý Module.
 * Tuân thủ SRP: Chỉ chứa nghiệp vụ liên quan đến Module.
 */
@Service
@RequiredArgsConstructor
public class ModuleApplicationService implements ManageModuleUseCase, GetModuleUseCase {

    private final ModuleRepositoryPort moduleRepository;
    private final CourseRepositoryPort courseRepository;
    private final ModuleMapper moduleMapper;

    // ===== READ USE CASES =====
    public List<ModuleResponse> getModulesByCourseId(Long courseId){
        if(courseRepository.findById(courseId).isEmpty()){
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND,"Course not found this ID: " + courseId);
        }
        List<Module> modules = moduleRepository.findAllByCourseIdOrderBySortOrder(courseId);
        return moduleMapper.toResponseList(modules);
    }
    public ModuleResponse getModuleById(Long moduleId){
        Module module = moduleRepository.findById(moduleId).orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND,"Module not found with ID: "+ moduleId));
        return moduleMapper.toResponse(module);
    }

    // ===== COMMAND USE CASES (WRITE) =====
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ModuleResponse addModule(Long courseId, ModuleUpsertRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND,"Course not found with ID: " + courseId));

        SecurityUtils.checkOwnership(course.getInstructor());

        Module module = Module.create(course, request.getTitle(), request.getSortOrder());
        return moduleMapper.toResponse(moduleRepository.save(module));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ModuleResponse updateModule(Long moduleId, ModuleUpsertRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND,"Module not found with id: " + moduleId));

        SecurityUtils.checkOwnership(module.getCourse().getInstructor());

        module.updateDetails(request.getTitle(), request.getSortOrder());
        return moduleMapper.toResponse(moduleRepository.save(module));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public void deleteModule(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MODULE_NOT_FOUND,"Module not found with id: " + moduleId));

        SecurityUtils.checkOwnership(module.getCourse().getInstructor());

        moduleRepository.deleteById(moduleId);
    }
}