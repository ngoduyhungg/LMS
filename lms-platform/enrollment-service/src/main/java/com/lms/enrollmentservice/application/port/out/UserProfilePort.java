package com.lms.enrollmentservice.application.port.out;

import com.lms.enrollmentservice.application.port.out.dto.UserProfile;

import java.util.List;

public interface UserProfilePort {
    UserProfile getProfile(String userId);
    List<UserProfile> getProfiles(List<String> userIds);
}
