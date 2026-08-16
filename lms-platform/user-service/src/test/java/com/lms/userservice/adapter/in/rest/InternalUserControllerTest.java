package com.lms.userservice.adapter.in.rest;

import com.lms.userservice.adapter.in.rest.dto.InternalUserProfileResponse;
import com.lms.userservice.adapter.in.rest.mapper.UserRestMapper;
import com.lms.userservice.application.port.in.GetInternalUserProfileUseCase;
import com.lms.userservice.domain.model.User;
import com.lms.userservice.domain.model.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalUserControllerTest {

    @Mock
    private GetInternalUserProfileUseCase getUseCase;

    @Mock
    private UserRestMapper mapper;

    @InjectMocks
    private InternalUserController controller;

    @Test
    @DisplayName("getInternalProfile - Luồng Controller hoạt động chuẩn xác khi lấy Profile")
    void getInternalProfile_ReturnsResponse() {
        // Given
        String rawUserId = "e2432dfe-91ad-4324-a1b7-1baee2e2885a";
        UserId userId = new UserId(rawUserId);

        User mockUser = User.builder()
                .id(userId)
                .email("student@mail.com")
                .fullName("Test Student")
                .build();

        InternalUserProfileResponse mockResponse = InternalUserProfileResponse.builder()
                .userId(rawUserId)
                .email("student@mail.com")
                .fullName("Test Student")
                .build();

        // Mocks behavior
        when(getUseCase.getInternalProfile(any(UserId.class))).thenReturn(mockUser);
        when(mapper.toInternalProfileResponse(mockUser)).thenReturn(mockResponse);

        // When
        InternalUserProfileResponse result = controller.getInternalProfile(rawUserId);

        // Then
        assertNotNull(result);
        assertEquals(rawUserId, result.getUserId());
        assertEquals("student@mail.com", result.getEmail());

        // Verify interactions
        verify(getUseCase, times(1)).getInternalProfile(any(UserId.class));
        verify(mapper, times(1)).toInternalProfileResponse(mockUser);
    }
}