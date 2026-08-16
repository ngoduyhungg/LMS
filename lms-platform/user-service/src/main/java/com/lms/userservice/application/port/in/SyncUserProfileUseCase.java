package com.lms.userservice.application.port.in;

import com.lms.userservice.application.port.in.command.SyncUserCommand;
import com.lms.userservice.domain.model.User;

public interface SyncUserProfileUseCase {
    User syncProfile(SyncUserCommand command);
}