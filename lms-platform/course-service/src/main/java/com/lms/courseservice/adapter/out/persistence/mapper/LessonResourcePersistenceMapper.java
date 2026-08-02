package com.lms.courseservice.adapter.out.persistence.mapper;

import com.lms.courseservice.adapter.out.persistence.entity.LessonResourceJpaEntity;
import com.lms.courseservice.domain.model.LessonResource;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface LessonResourcePersistenceMapper {
    @Mapping(target = "lesson", ignore = true) // Cắt circular reference
    @Mapping(target = "auditInfo.createdAt", source = "createdAt")
    @Mapping(target = "auditInfo.updatedAt", source = "updatedAt")
    LessonResource toDomain(LessonResourceJpaEntity entity);

    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    LessonResourceJpaEntity toEntity(LessonResource domain);
}
