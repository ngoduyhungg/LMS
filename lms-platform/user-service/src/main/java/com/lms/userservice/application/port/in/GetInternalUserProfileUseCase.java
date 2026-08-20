package com.lms.userservice.application.port.in;

import com.lms.userservice.domain.model.User;
import com.lms.userservice.domain.model.UserId;

public interface GetInternalUserProfileUseCase {
    User getInternalProfile(UserId userId);
}