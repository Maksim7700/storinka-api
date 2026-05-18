package ua.storinka.backend.dto;

import ua.storinka.backend.entity.User;
import ua.storinka.backend.enums.Role;
import ua.storinka.backend.enums.UserPlan;
import ua.storinka.backend.enums.UserStatus;

public record UserDto(
        Long id,
        String email,
        String fullName,
        String phone,
        String avatarUrl,
        Role role,
        UserPlan plan,
        UserStatus status,
        boolean emailVerified
) {
    public static UserDto from(User u) {
        return new UserDto(
                u.getId(),
                u.getEmail(),
                u.getFullName(),
                u.getPhone(),
                u.getAvatarUrl(),
                u.getRole(),
                u.getPlan(),
                u.getStatus(),
                u.isEmailVerified()
        );
    }
}
