package com.lms.enrollmentservice.adapter.out.persistence.mapper;

import com.lms.enrollmentservice.adapter.out.persistence.entity.EnrollmentJpaEntity;
import com.lms.enrollmentservice.domain.model.Enrollment;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {LessonProgressPersistenceMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface EnrollmentPersistenceMapper {

    @Mapping(target = "auditInfo.createdAt", source = "createdAt")
    @Mapping(target = "auditInfo.updatedAt", source = "updatedAt")
    Enrollment toDomain(EnrollmentJpaEntity entity);

    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    EnrollmentJpaEntity toEntity(Enrollment domain);

    @AfterMapping
    default void linkLessonProgresses(@MappingTarget EnrollmentJpaEntity entity) {
        if (entity.getLessonProgresses() != null) {
            entity.getLessonProgresses().forEach(lessonProgress -> lessonProgress.setEnrollment(entity));
        }
    }
}
