package ua.storinka.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.storinka.backend.dto.ChangePasswordRequest;
import ua.storinka.backend.dto.UpdateProfileRequest;
import ua.storinka.backend.dto.UserDto;
import ua.storinka.backend.entity.User;
import ua.storinka.backend.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public UserDto get(@AuthenticationPrincipal User currentUser) {
        return profileService.getProfile(currentUser);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateProfileRequest req,
                          @AuthenticationPrincipal User currentUser) {
        return profileService.updateProfile(currentUser, req);
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordRequest req,
                                @AuthenticationPrincipal User currentUser) {
        profileService.changePassword(currentUser, req);
    }
}
