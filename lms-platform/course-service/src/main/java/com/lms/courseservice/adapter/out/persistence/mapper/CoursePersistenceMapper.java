package com.lms.courseservice.adapter.out.persistence.mapper;

import com.lms.courseservice.adapter.out.persistence.entity.CourseJpaEntity;
import com.lms.courseservice.domain.model.Course;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {CategoryPersistenceMapper.class, ModulePersistenceMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,builder = @Builder(disableBuilder = true)
)
public interface CoursePersistenceMapper {

    @Mapping(target = "category", source = "category")
    @Mapping(target = "auditInfo.createdAt", source = "createdAt")
    @Mapping(target = "auditInfo.updatedAt", source = "updatedAt")
    Course toDomain(CourseJpaEntity entity);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    CourseJpaEntity toEntity(Course domain);

    @AfterMapping
    default void setModuleBackReferences(
            @MappingTarget CourseJpaEntity entity
    ) {
        if (entity.getModules() == null) {
            return;
        }

        entity.getModules().forEach(module ->
                module.setCourse(entity)
        );
    }

}
