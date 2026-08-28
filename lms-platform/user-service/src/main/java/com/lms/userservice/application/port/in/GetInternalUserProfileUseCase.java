package com.lms.userservice.application.port.in;

import com.lms.userservice.domain.model.User;
import com.lms.userservice.domain.model.UserId;

import java.util.List;
import java.util.Set;

public interface GetInternalUserProfileUseCase {
    User getInternalProfile(UserId userId);
    List<User> getBatchInternalProfiles(Set<UserId> userIds);
}