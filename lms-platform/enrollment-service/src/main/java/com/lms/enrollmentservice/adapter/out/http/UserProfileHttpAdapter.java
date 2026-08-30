package com.lms.enrollmentservice.adapter.out.http;

import com.lms.enrollmentservice.adapter.out.http.dto.UserBatchProfileRequest;
import com.lms.enrollmentservice.application.port.out.UserProfilePort;
import com.lms.enrollmentservice.application.port.out.dto.UserProfile;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileHttpAdapter implements UserProfilePort {

    private final RestClient userServiceClient;

    @Override
    public UserProfile getProfile(String userId) {
        try {
            return userServiceClient.get()
                    .uri("/api/internal/users/{userId}/profile", userId)
                    .retrieve()
                    .body(UserProfile.class);
        } catch (RestClientResponseException e) {
            log.error("Failed to fetch user profile from user-service for userId: {}. Status: {}", userId, e.getStatusCode());
            if (e.getStatusCode().value() == 404) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Network or timeout error when calling user-service for userId: {}", userId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public List<UserProfile> getProfiles(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();
        try {
            return userServiceClient.post()
                    .uri("/api/internal/users/profiles/batch")
                    .body(new UserBatchProfileRequest(userIds))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UserProfile>>() {});
        } catch (Exception e) {
            log.error("Failed to fetch batch user profiles", e);
            return List.of();
        }
    }
}