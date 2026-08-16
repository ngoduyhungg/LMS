package com.lms.enrollmentservice.adapter.out.persistence.mapper;

import com.lms.enrollmentservice.adapter.out.persistence.entity.CourseReferenceJpaEntity;
import com.lms.enrollmentservice.domain.model.CourseReference;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface CourseReferencePersistenceMapper {
    CourseReference toDomain(CourseReferenceJpaEntity entity);
    CourseReferenceJpaEntity toEntity(CourseReference domain);
}