package com.lms.userservice.adapter.in.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchInternalUserProfileRequest {

    @NotEmpty(message = "User IDs list cannot be empty")
    @Size(max = 100, message = "Cannot fetch more than 100 profiles at once")
    private Set<String> userIds;
}