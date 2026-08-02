package com.lms.courseservice.adapter.out.persistence.mapper;

import com.lms.courseservice.adapter.out.persistence.entity.LessonJpaEntity;
import com.lms.courseservice.domain.model.Lesson;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {LessonResourcePersistenceMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface LessonPersistenceMapper {
    @Mapping(target = "module", ignore = true) // Cắt circular reference
    @Mapping(target = "auditInfo.createdAt", source = "createdAt")
    @Mapping(target = "auditInfo.updatedAt", source = "updatedAt")
    Lesson toDomain(LessonJpaEntity entity);

    @Mapping(target = "module", ignore = true)
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    LessonJpaEntity toEntity(Lesson domain);
}
