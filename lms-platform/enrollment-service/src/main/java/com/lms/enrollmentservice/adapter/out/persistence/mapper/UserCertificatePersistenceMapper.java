package com.lms.enrollmentservice.adapter.out.persistence.mapper;

import com.lms.enrollmentservice.adapter.out.persistence.entity.UserCertificateJpaEntity;
import com.lms.enrollmentservice.domain.model.UserCertificate;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface UserCertificatePersistenceMapper {
    UserCertificate toDomain(UserCertificateJpaEntity entity);
    UserCertificateJpaEntity toEntity(UserCertificate domain);
}