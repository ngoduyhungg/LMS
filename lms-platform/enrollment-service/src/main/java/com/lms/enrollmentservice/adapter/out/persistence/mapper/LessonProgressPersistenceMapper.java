package com.lms.enrollmentservice.adapter.out.persistence.mapper;

import com.lms.enrollmentservice.adapter.out.persistence.entity.LessonProgressJpaEntity;
import com.lms.enrollmentservice.domain.model.LessonProgress;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface LessonProgressPersistenceMapper {

    // BỎ QUA mapping property 'enrollment' để không bị lặp vô tận (Circular Reference)
    @Mapping(target = "enrollment", ignore = true)
    LessonProgressJpaEntity toEntity(LessonProgress domain);

    LessonProgress toDomain(LessonProgressJpaEntity entity);
}
