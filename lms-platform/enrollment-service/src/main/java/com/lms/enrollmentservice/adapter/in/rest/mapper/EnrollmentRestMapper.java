package com.lms.enrollmentservice.adapter.in.rest.mapper;

import com.lms.enrollmentservice.adapter.in.rest.dto.EnrollRequest;
import com.lms.enrollmentservice.adapter.in.rest.dto.EnrollmentResponse;
import com.lms.enrollmentservice.adapter.in.rest.dto.LessonProgressResponse;
import com.lms.enrollmentservice.adapter.in.rest.dto.TrackProgressRequest;
import com.lms.enrollmentservice.application.port.in.command.EnrollCommand;
import com.lms.enrollmentservice.application.port.in.command.TrackProgressCommand;
import com.lms.enrollmentservice.domain.model.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EnrollmentRestMapper {

    // Map từ Request DTO + userId (từ Token) -> Command
    default EnrollCommand toCommand(EnrollRequest request, String userId) {
        return new EnrollCommand(userId, request.courseId());
    }

    default TrackProgressCommand toCommand(TrackProgressRequest request, String userId, Long courseId) {
        return new TrackProgressCommand(
                userId,
                courseId,
                request.lessonId(),
                request.watchedSeconds(),
                request.isCompleted()
        );
    }

    // Map từ Domain -> Response DTO
    @Mapping(target = "status", expression = "java(domain.getStatus().name())")
    EnrollmentResponse toResponse(Enrollment domain);

    @Mapping(target = "status", expression = "java(progress.getStatus().name())")
    LessonProgressResponse toLessonProgressResponse(com.lms.enrollmentservice.domain.model.LessonProgress progress);

    List<EnrollmentResponse> toResponseList(List<Enrollment> domains);
}
