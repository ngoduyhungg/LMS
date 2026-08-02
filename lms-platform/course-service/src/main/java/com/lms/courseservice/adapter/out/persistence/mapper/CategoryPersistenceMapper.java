package com.lms.courseservice.adapter.out.persistence.mapper;

import com.lms.courseservice.adapter.out.persistence.entity.CategoryJpaEntity;
import com.lms.courseservice.domain.model.Category;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface CategoryPersistenceMapper {

    @Mapping(target = "auditInfo.createdAt", source = "createdAt")
    @Mapping(target = "auditInfo.updatedAt", source = "updatedAt")
    Category toDomain(CategoryJpaEntity entity);

    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    CategoryJpaEntity toEntity(Category domain);
}
