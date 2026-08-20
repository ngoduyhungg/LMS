package com.lms.enrollmentservice.adapter.in.rest.mapper;

import com.lms.enrollmentservice.adapter.in.rest.dto.CertificateTemplateRequest;
import com.lms.enrollmentservice.adapter.in.rest.dto.CertificateTemplateResponse;
import com.lms.enrollmentservice.application.port.in.command.UpsertCertificateCommand;
import com.lms.enrollmentservice.domain.model.Certificate;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InstructorCertificateRestMapper {

    default UpsertCertificateCommand toCommand(CertificateTemplateRequest request, Long courseId, String currentUserId) {
        return new UpsertCertificateCommand(courseId, request.title(), request.templateUrl(), currentUserId);
    }

    CertificateTemplateResponse toResponse(Certificate domain);
}