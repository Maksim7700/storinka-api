package ua.storinka.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank @Size(max = 255) String fullName,
        @Size(max = 32) String phone,
        @Size(max = 512) String avatarUrl
) {}
