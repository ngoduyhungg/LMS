package com.lms.courseservice.adapter.out.persistence.mapper;

import com.lms.courseservice.adapter.out.persistence.entity.ModuleJpaEntity;
import com.lms.courseservice.domain.model.Module;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {LessonPersistenceMapper.class}, builder = @Builder(disableBuilder = true))
public interface ModulePersistenceMapper {

    @Mapping(target = "course", ignore = true) // Tránh lặp vô hạn
    @Mapping(target = "auditInfo.createdAt", source = "createdAt")
    @Mapping(target = "auditInfo.updatedAt", source = "updatedAt")
    Module toDomain(ModuleJpaEntity entity);

    @Mapping(target = "course", ignore = true) // Course sẽ được mapping từ phía CourseJpaEntity
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    ModuleJpaEntity toEntity(Module domain);
}
