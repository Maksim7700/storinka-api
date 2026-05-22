package ua.storinka.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ua.storinka.backend.dto.ChangePasswordRequest;
import ua.storinka.backend.dto.UpdateProfileRequest;
import ua.storinka.backend.dto.UserDto;
import ua.storinka.backend.entity.User;
import ua.storinka.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDto getProfile(User currentUser) {
        return UserDto.from(currentUser);
    }

    @Transactional
    public UserDto updateProfile(User currentUser, UpdateProfileRequest req) {
        // Reattach: principal may be detached if a request reused a cached
        // User. findById returns the managed instance, ensuring updated_at and
        // dirty checking work.
        User managed = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
        managed.setFullName(req.fullName());
        managed.setPhone(blankToNull(req.phone()));
        managed.setAvatarUrl(blankToNull(req.avatarUrl()));
        return UserDto.from(managed);
    }

    @Transactional
    public void changePassword(User currentUser, ChangePasswordRequest req) {
        User managed = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
        if (managed.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No password is set on this account. Sign in with Google.");
        }
        if (!passwordEncoder.matches(req.currentPassword(), managed.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Current password is incorrect");
        }
        managed.setPassword(passwordEncoder.encode(req.newPassword()));
    }

    private static String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
