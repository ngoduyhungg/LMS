package com.lms.userservice.application.port.out;

import com.lms.userservice.domain.model.User;
import com.lms.userservice.domain.model.UserId;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(UserId userId);
    User save(User user);
}