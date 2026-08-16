package com.lms.enrollmentservice.adapter.out.persistence.mapper;

import com.lms.enrollmentservice.adapter.out.persistence.entity.CertificateJpaEntity;
import com.lms.enrollmentservice.domain.model.Certificate;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface CertificatePersistenceMapper {

    @Mapping(target = "auditInfo.createdAt", source = "createdAt")
    @Mapping(target = "auditInfo.updatedAt", source = "updatedAt")
    Certificate toDomain(CertificateJpaEntity entity);

    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    CertificateJpaEntity toEntity(Certificate domain);
}