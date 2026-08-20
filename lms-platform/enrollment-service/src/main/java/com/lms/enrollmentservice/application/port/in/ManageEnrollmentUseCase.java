package com.lms.enrollmentservice.application.port.in;

import com.lms.enrollmentservice.application.port.in.command.EnrollCommand;
import com.lms.enrollmentservice.application.port.in.command.TrackProgressCommand;
import com.lms.enrollmentservice.domain.model.Enrollment;

public interface ManageEnrollmentUseCase {
    Enrollment enrollUser(EnrollCommand command);
    Enrollment trackLessonProgress(TrackProgressCommand command);
}