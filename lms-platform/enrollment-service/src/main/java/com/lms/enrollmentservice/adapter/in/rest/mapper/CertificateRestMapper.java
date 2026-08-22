package com.lms.enrollmentservice.adapter.in.rest.mapper;

import com.lms.enrollmentservice.adapter.in.rest.dto.UserCertificateResponse;
import com.lms.enrollmentservice.domain.model.UserCertificate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CertificateRestMapper {

    @Mapping(target = "pdfUrl", expression = "java(\"/api/certificates/\" + domain.getCertificateCode() + \"/download\")")
    UserCertificateResponse toResponse(UserCertificate domain);

    List<UserCertificateResponse> toResponseList(List<UserCertificate> domains);
}