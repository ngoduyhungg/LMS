package com.lms.enrollmentservice.application.port.out;

import com.lms.enrollmentservice.application.port.out.dto.UserProfile;

public interface UserProfilePort {
    UserProfile getProfile(String userId);
}
