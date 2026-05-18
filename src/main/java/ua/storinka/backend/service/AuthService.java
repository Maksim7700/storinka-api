package ua.storinka.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ua.storinka.backend.dto.AuthResponse;
import ua.storinka.backend.dto.LoginRequest;
import ua.storinka.backend.dto.RegisterRequest;
import ua.storinka.backend.dto.UserDto;
import ua.storinka.backend.entity.User;
import ua.storinka.backend.enums.UserStatus;
import ua.storinka.backend.repository.UserRepository;
import ua.storinka.backend.security.JwtService;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        User user = userRepository.save(User.builder()
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .phone(req.phone())
                .build());
        try {
            emailVerificationService.issueAndSend(user);
        } catch (MailException e) {
            // Don't block registration if mail service is unreachable;
            // user can be resent a verification email later.
            log.warn("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage());
        }
        return new AuthResponse(jwtService.generate(user.getId(), user.getEmail()), UserDto.from(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account suspended");
        }
        if (user.getPassword() == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return new AuthResponse(jwtService.generate(user.getId(), user.getEmail()), UserDto.from(user));
    }
}
